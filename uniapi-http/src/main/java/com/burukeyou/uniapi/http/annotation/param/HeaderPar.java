package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * 支持标记的参数类型举例:
 *       对象              @HeaderPar         User
 *       Map              @HeaderPar         Map
 *       普通值            @HeaderPar("id")   String
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderPar {

    /**
     * 单个请求头键值对的key名
     */
    String value() default "";
}
