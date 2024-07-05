package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
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
