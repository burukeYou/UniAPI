package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 * 本身不是对Http请求内容的配置，仅用于标记一个对象，然后会对该对象内的所有标记了其他@Par注解的字段进行解析处理，
 *
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
