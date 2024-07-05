package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 *  支持标记的参数类型举例
 *          InputStream
 *          File
 *          InputStreamSource
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyBinaryPar {

}
