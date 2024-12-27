package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;


/**
 * Mark the HTTP request body content in JSON format: corresponding content type is application/JSON
 *
 * <pre>
 * Support parameter types for tags：
 *          Custom Object
 *          Custom Object Collection
 *          Map
 *          Normal value
 *          Normal value Collection
 *</pre>
 *
 * If a @BodyJsonPar.name is specified, it will be added to the field path of the final JSON request body，
 * If not specified, it is treated as the entire JSON request body
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyJsonPar {

    /**
     * As a path field name in the JSON request body, simple names and JSON paths are supported
     */
    @AliasFor("name")
    String value() default "";

    /**
     *  As a path field name in the JSON request body, simple names and JSON paths are supported
     */
    @AliasFor("value")
    String name() default "";

}
