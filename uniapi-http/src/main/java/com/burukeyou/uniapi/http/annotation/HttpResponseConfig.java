package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http Response Config
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpResponseConfig {

    /**
     *  If the response format of the http-interface is JSON,
     *  and you want to convert some fields of the JSON from JSON strings to JSON objects,
     *  you can use this method to set the path list of the JSON fields to be converted
     */
     String[] jsonPathUnPack() default {};
}
