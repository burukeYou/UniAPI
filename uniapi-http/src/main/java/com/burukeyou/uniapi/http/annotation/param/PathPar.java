package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * 标记Http请求路径变量参数
 *
 * 支持标记的参数类型举例:
 *       普通值            @PathPar("id")   String
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathPar {

    String value() default "";
}
