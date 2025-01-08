package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Mark the HTTP request body content in XML format: corresponding content type is application/xml
 *
 * <pre>
 * Support parameter types for tags： Custom Object
 *
 *</pre>
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyXmlPar {


}
