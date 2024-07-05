package com.burukeyou.uniapi.http.annotation.param;

import java.lang.annotation.*;

/**
 *
 *   一个对象组合所有参数， 如果对象内的字段不标记具体的param注解，默认 全变量都可使用只要变量名一致
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComposePar {

}
