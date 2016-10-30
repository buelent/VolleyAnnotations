package li.ulas.annotation.volley.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import li.ulas.annotations.volley.Body;
import li.ulas.annotations.volley.Headers;
import li.ulas.annotations.volley.Path;
import li.ulas.annotations.volley.Query;
import li.ulas.annotations.volley.VolleyClient;

import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;

/**
 * Created by bul on 04.10.16 .
 * ---
 */
@AutoService(Processor.class)
public class VolleyClientProcessor extends AbstractProcessor {

	private Messager messager;


	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return singleton(VolleyClient.class.getCanonicalName());
	}


	@Override
	public SourceVersion getSupportedSourceVersion() {
		return latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		messager = processingEnv.getMessager();

		List<ClazzInfo> classes = new ArrayList<>();
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(VolleyClient.class)) {

			TypeElement annotatedClass = (TypeElement) annotatedElement;
			VolleyClient annotation = annotatedClass.getAnnotation(VolleyClient.class);
			String templateFile = annotation.templateName();
			String projectDir = processingEnv.getOptions().get("projectRoot");
			List<HTTPRequest> methods = new ArrayList<>();
			ClazzInfo clazzInfo = new ClazzInfo(annotatedElement).invoke();
			clazzInfo.setMethods(methods);
			clazzInfo.templatePath = projectDir + File.separator + templateFile;

			List<? extends Element> classMembers = annotatedClass.getEnclosedElements();
			for (Element classMember : classMembers) {
				if (classMember.getKind() == ElementKind.METHOD) {
					//Methods
					List<? extends AnnotationMirror> annotationMirrors = classMember.getAnnotationMirrors();
					methods.add(extractRequestStub((ExecutableElement) classMember, annotationMirrors));

				}
			}
			classes.add(clazzInfo);

		}

		StringBuilder sb = new StringBuilder("");
		for (ClazzInfo aClass : classes) {
			createImplFile(aClass);
			sb.append("if(clazz.equals(").append(aClass.className).append(".class").append(")){\n return (T)new ").append(aClass.implClass).append("(this.apiBaseUrl, this.keystoreManager);\n}\n");

		}
		messager.printMessage(Diagnostic.Kind.NOTE,
				"Printing: Factory  " + sb);

		return true;
	}

	private HTTPRequest extractRequestStub(ExecutableElement classMember, List<? extends AnnotationMirror> mirrors) {
		HTTPRequest req = new HTTPRequest();
		List<String> params = new ArrayList<>();
		for (VariableElement element : classMember.getParameters()) {
			params.add(element.asType().toString() + " " + element.getSimpleName().toString());
		}
		String methodToImplement = "public " + classMember.getReturnType().toString() + classMember.getSimpleName() + "(" + Joiner.on(",").join(params) + ")";

		req.setMethodToImplement(methodToImplement);
		for (AnnotationMirror mirror : mirrors) {
			DeclaredType annoType = mirror.getAnnotationType();
			Element annoElement = annoType.asElement();
			String method = annoElement.getSimpleName().toString();
			Integer methodForName = HTTPRequest.getMethodForName(method);
			if (methodForName == null) {
				if (Headers.class.getSimpleName().equalsIgnoreCase(method)) {
					req.setHeaders(parseHeaders(mirror));
				} else {
					throw new RuntimeException("Unknown method " + method);

				}
			} else {
				req.setMethod(methodForName);
				req.setMethodName(method);
				Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
					if ("value()".equalsIgnoreCase(entry.getKey().toString())) {
						req.setRawUrl(entry.getValue().getValue().toString());
					}

				}
				ExecutableElement classMemberEx = classMember;
				List<? extends TypeParameterElement> typeParameters = classMemberEx.getTypeParameters();
				DeclaredType returnType = (DeclaredType) classMemberEx.getReturnType();
				req.setReturnTypeRaw(returnType.asElement().toString());
				req.setReturnTypeParameter(returnType.getTypeArguments().get(0).toString());
				//process parameters
				//	messager.printMessage(Diagnostic.Kind.NOTE,
				//			"Printing: Method found " + returnType.toString());
				List<? extends VariableElement> parameters = classMemberEx.getParameters();
				for (VariableElement parameter : parameters) {
					List<? extends AnnotationMirror> paraMirrors = parameter.getAnnotationMirrors();
					for (AnnotationMirror paramAnno : paraMirrors) {
						DeclaredType annotationType = paramAnno.getAnnotationType();
						String paramAnnoName = annotationType.asElement().getSimpleName().toString();
						if (paramAnnoName.equals(Body.class.getSimpleName())) {
							req.setBodyClazzType(parameter.asType().toString());
							req.setBodyParamName(parameter.getSimpleName().toString());
						}
						if (paramAnnoName.equals(Path.class.getSimpleName())) {
							PathVariable pathVariable = new PathVariable(parameter.asType().toString(), parameter.getSimpleName().toString());
							for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : paramAnno.getElementValues().entrySet()) {
								if ("value()".equalsIgnoreCase(entry.getKey().toString())) {
									pathVariable.setPlaceholderName(entry.getValue().getValue().toString());
								}

							}
							req.addPathVariable(pathVariable);
						}
						if (paramAnnoName.equals(Query.class.getSimpleName())) {

							Query[] annotationsByType = parameter.getAnnotationsByType(Query.class);
							if (annotationsByType != null) {
								for (Query query : annotationsByType) {
									QueryParameter queryParameter = new QueryParameter(parameter.asType().toString(), parameter.getSimpleName().toString());
									queryParameter.setEncoded(query.encoded());
									queryParameter.setPlaceholderName(query.value());
								/*	messager.printMessage(Diagnostic.Kind.NOTE,
											"Printing: Query  " + query);
											*/
									req.addQueryParameter(queryParameter);

								}


							}

						}


					}
				}

			}
		}

		return req;
	}


	private Map<String, String> parseHeaders(AnnotationMirror mirror) {
		Map<String, String> headers = new HashMap<>();
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			if ("value()".equalsIgnoreCase(entry.getKey().toString())) {
				List<Object> valuesAsList = (List<Object>) entry.getValue().getValue();
				for (Object o : valuesAsList) {
					String s = o.toString().replaceAll("\"", "");
					String[] split = s.split(":");
					headers.put(split[0].trim(), split[1].trim());
				}
			/*	messager.printMessage(Diagnostic.Kind.NOTE,
						"Printing: Method found " + headers);
						*/
			}

		}
		return headers;
	}

	private class ClazzInfo {
		private Element annotatedElement;
		private PackageElement packageElement;
		private String className;
		private String packageName;
		private List<HTTPRequest> methods;
		public String templatePath;
		public String implClass;

		public ClazzInfo(Element annotatedElement) {
			this.annotatedElement = annotatedElement;
			this.packageElement = (PackageElement) annotatedElement.getEnclosingElement();
		}

		public String getClassName() {
			return className;
		}

		public String getPackageName() {
			return packageName;
		}

		public ClazzInfo invoke() {
			className = annotatedElement.getSimpleName().toString();
			implClass = className + "Impl";
			packageName = packageElement.getQualifiedName().toString();
			return this;
		}

		public void setMethods(List<HTTPRequest> methods) {
			this.methods = methods;
		}

		public List<HTTPRequest> getMethods() {
			return methods;
		}

	}


	private void createImplFile(ClazzInfo info) {
		BufferedWriter writer = null;
		try {


			for (HTTPRequest method : info.methods) {
				List<PathVariable> pathVariables = method.getPathVariables();
				String url = method.getRawUrl();
				for (PathVariable variable : pathVariables) {
					url = url.replaceAll("\\{" + variable.getPlaceholderName() + "\\}", "\"+" + variable.getName() + "+\"");
				}
				method.setUrl(url);
			}

			JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(info.packageName + "." + info.implClass);
			writer = new BufferedWriter(sourceFile.openWriter());
			JtwigModel model = JtwigModel.newModel().with("info", info);
			JtwigTemplate template = JtwigTemplate.fileTemplate(info.templatePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			template.render(model, baos);
			writer.write(baos.toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not write source for " + info.className, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					//Silent
				}
			}
		}
	}

}

