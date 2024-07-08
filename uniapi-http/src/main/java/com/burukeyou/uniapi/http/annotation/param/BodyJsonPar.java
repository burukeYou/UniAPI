package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;


/**
 * 标记Http请求体内容为json形式: 对应content-type为 application/json
 *
 * 支持标记的参数类型举例:
 *       对象              @BodyJsonPar   User
 *       对象集合           @BodyJsonPar   List<User>
 *       Map               @BodyJsonPar   Map
 *       普通值            @BodyJsonPar   String
 *       普通值集合         @BodyJsonPar   Integer[]
 *
 *
 * @author caizhihao
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyJsonPar {

}
