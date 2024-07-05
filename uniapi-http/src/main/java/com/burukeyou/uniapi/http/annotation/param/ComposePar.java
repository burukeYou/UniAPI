package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * 支持标记的参数类型举例:
 *       对象              @ComposePar  User
 *
 *
 *
 * @author caizhihao
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComposePar {

}
