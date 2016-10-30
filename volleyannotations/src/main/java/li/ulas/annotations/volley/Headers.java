package li.ulas.annotations.volley;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Adds headers literally supplied in the {@code value}.
 * <p>
 * <pre>{@code
 * &#64;Headers("Cache-Control: max-age=640000")
 * &#64;GET("/")
 * ...
 *
 * &#64;Headers({
 *   "X-Foo: Bar",
 *   "X-Ping: Pong"
 * })
 * &#64;GET("/")
 * ...
 * }</pre>
 * <p>
 * <strong>Note:</strong> Headers do not overwrite each other. All headers with the same name will
 * be included in the request.
 *
 * @author Adrian Cole (adrianc@netflix.com)
 */
@Documented
@Target(METHOD)
@Retention(SOURCE)
public @interface Headers {
	String[] value();
}