package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * 标记Http请求url的查询参数
 *
 *  支持的参数类型举例
 *     普通值集合   @QueryPar("ids")     List<Integer>
 *     对象        @QueryPar            User
 *     map        @QueryPar            Map
 *     普通值      @QueryPar("id")      String
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryPar {

    String value() default "";

}
