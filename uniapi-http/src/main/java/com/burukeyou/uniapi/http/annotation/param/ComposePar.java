package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * It is not a configuration for HTTP request content itself,
 * but is only used to mark an object, and then all fields marked with
 * other @Par annotations within that object will be parsed and processed
 *
 * <pre>Support parameter types for tags only  Custom Object</pre>

 *
 * @author caizhihao
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComposePar {

}
