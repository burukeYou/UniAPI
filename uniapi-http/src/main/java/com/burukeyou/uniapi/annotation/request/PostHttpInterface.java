package com.burukeyou.uniapi.annotation.request;


import com.burukeyou.uniapi.support.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** Post 接口配置
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpInterface(method = RequestMethod.POST)
public @interface PostHttpInterface {

    /**
     * 接口路径
     */
    @AliasFor(annotation = HttpInterface.class)
    String path() default "";

    /**
     * 接口路径
     */
    @AliasFor(annotation = HttpInterface.class)
    String value() default "";

    @AliasFor(annotation = HttpInterface.class)
    String[] headers() default {};

    @AliasFor(annotation = HttpInterface.class)
    String[] params() default {};

    @AliasFor(annotation = HttpInterface.class)
    String cookie() default "";

}
