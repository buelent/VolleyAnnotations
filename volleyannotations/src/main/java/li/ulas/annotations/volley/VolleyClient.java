package li.ulas.annotations.volley;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bul on 04.10.16 .
 * ---
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface VolleyClient {
	String templateName();
}
