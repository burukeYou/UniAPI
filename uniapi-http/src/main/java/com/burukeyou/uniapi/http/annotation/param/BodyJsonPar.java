package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * Mark the HTTP request body content in JSON format: corresponding content type is application/JSON
 *
 * Support parameter types for tags：
 *          Custom Object
 *          Custom Object Collection
 *          Map
 *          Normal value
 *          Normal value Collection
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyJsonPar {

}
