package li.ulas.annotation.volley.processor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPRequest {
	public static int GET = 0;
	public static int POST = 1;
	public static int PUT = 2;
	public static int DELETE = 3;
	//public static int HEAD = 4;
	//public static int OPTIONS = 5;
	//public static int TRACE = 6;
	public static int PATCH = 7;

	private static final BiMap<String, Integer> methodNameToMap = HashBiMap.create();

	static {
		methodNameToMap.put(li.ulas.annotations.volley.POST.class.getSimpleName(), POST);
		methodNameToMap.put(li.ulas.annotations.volley.GET.class.getSimpleName(), GET);
		methodNameToMap.put(li.ulas.annotations.volley.PUT.class.getSimpleName(), PUT);
		methodNameToMap.put(li.ulas.annotations.volley.DELETE.class.getSimpleName(), DELETE);
		methodNameToMap.put(li.ulas.annotations.volley.PATCH.class.getSimpleName(), PATCH);
	}

	private String rawUrl;
	private String url;
	private String methodName;
	private Integer method;
	private Map<String, String> headers = new HashMap<>();
	private String bodyClazzType;
	private String bodyParamName;
	private List<PathVariable> pathVariables = new ArrayList<>();
	private String returnTypeRaw;
	private String returnTypeParameter;
	private String methodToImplement;
	private List<QueryParameter> queryParams = new ArrayList<>();

	public static Integer getMethodForName(String name) {
		return methodNameToMap.get(name);
	}

	public void setRawUrl(String rawUrl) {
		this.url = rawUrl;
		this.rawUrl = rawUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRawUrl() {
		return rawUrl;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}

	public Integer getMethod() {
		return method;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers.putAll(headers);
	}

	public void setBodyClazzType(String bodyClazzType) {
		this.bodyClazzType = bodyClazzType;
	}

	public String getBodyClazzType() {
		return bodyClazzType;
	}

	public void setBodyParamName(String bodyParamName) {
		this.bodyParamName = bodyParamName;
	}

	public String getBodyParamName() {
		return bodyParamName;
	}

	public void addPathVariable(PathVariable pathVariable) {
		this.pathVariables.add(pathVariable);
	}

	public List<PathVariable> getPathVariables() {
		return pathVariables;
	}

	public void setPathVariables(List<PathVariable> pathVariables) {
		this.pathVariables = pathVariables;
	}

	public void setReturnTypeRaw(String returnTypeRaw) {
		this.returnTypeRaw = returnTypeRaw;
	}

	public String getReturnTypeRaw() {
		return returnTypeRaw;
	}

	public void setReturnTypeParameter(String returnTypeParameter) {
		this.returnTypeParameter = returnTypeParameter;
	}

	public String getReturnTypeParameter() {
		return returnTypeParameter;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HTTPRequest{");
		sb.append("bodyClazzType='").append(bodyClazzType).append('\'');
		sb.append(", rawUrl='").append(rawUrl).append('\'');
		sb.append(", url='").append(url).append('\'');
		sb.append(", methodName='").append(methodName).append('\'');
		sb.append(", method=").append(method);
		sb.append(", headers=").append(headers);
		sb.append(", bodyParamName='").append(bodyParamName).append('\'');
		sb.append(", pathVariables=").append(pathVariables);
		sb.append(", returnTypeRaw='").append(returnTypeRaw).append('\'');
		sb.append(", returnTypeParameter='").append(returnTypeParameter).append('\'');
		sb.append(", methodToImplement='").append(methodToImplement).append('\'');
		sb.append(", queryParams=").append(queryParams);
		sb.append('}');
		return sb.toString();
	}

	public void setMethodToImplement(String methodToImplement) {
		this.methodToImplement = methodToImplement;
	}

	public String getMethodToImplement() {
		return methodToImplement;
	}

	public void addQueryParameter(QueryParameter var) {
		this.queryParams.add(var);
	}
}
