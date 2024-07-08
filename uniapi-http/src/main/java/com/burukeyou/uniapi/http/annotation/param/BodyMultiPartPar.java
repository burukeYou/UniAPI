package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * 标记Http请求体内容为复杂形式: 对应content-type为 multipart/form-data
 *
 * 支持标记的参数类型举例:
 *       对象              @BodyMultiPartPar  User
 *       Map              @BodyMultiPartPar  Map
 *       普通值            @BodyMultiPartPar String
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyMultiPartPar {

    String value() default "";
}
