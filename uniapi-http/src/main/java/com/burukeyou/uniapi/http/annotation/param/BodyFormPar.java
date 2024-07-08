package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * 标记Http请求体内容为普通表单形式: 对应content-type为 application/x-www-form-urlencoded
 *
 *
 * 支持标记的参数类型举例:
 *       对象                   @BodyFormPar  User
 *       Map                   @BodyFormPar  Map
 *       普通值                 @BodyFormPar("name") String
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyFormPar {

    String value() default "";

}
