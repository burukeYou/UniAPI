package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;

/**
 * Http Response Config
 *
 * @author caizhihao
 */

@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpResponseCfg {

    /**
     * <p>Unpacking of the original response body
     * <p>If the response format of the http-interface is JSON,
     *   and you want to convert some fields of the JSON from JSON strings to JSON objects,
     *   you can use this method to set the path list of the JSON fields to be converted
     *
     */
     String[] jsonPathUnPack() default {};

    /**
     * <p>Unpacking of the response body string after the {@link HttpApiProcessor#postAfterHttpResponseBodyString}
     * <p>if the response body string format of the http-interface is JSON, and you want to convert some fields of the JSON from JSON strings to JSON objects,
     *    you can use this method to set the path list of the JSON fields to be converted
     *
     * @see com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor#postAfterHttpResponseBodyString
     */
    String[] afterJsonPathUnPack() default {};

    /**
     * Use the json path value of http response body string as the actual json result
     * @return      json path
     */
    String extractJsonPath() default "";
}
