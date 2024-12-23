package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http Request Config
 *
 *  // todo implement
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestCfg {

    /**
     *  If the format of the Http request body is json，and you want to convert some fields of the JSON from objects to JSON String,
     *  you can use this method to set the path list of the JSON fields to be converted
     */
     String[] jsonPathPack() default {};


}
