package com.burukeyou.uniapi.http.annotation.request;

import com.burukeyou.uniapi.http.support.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** Http接口配置
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInterface {

    /**
     * 接口路径
     */
    @AliasFor("value")
    String path() default "";

    /**
     * 接口路径
     */
    @AliasFor("path")
    String value() default "";

    /**
     *  请求方式
     */
    RequestMethod method();

    String[] headers() default {};

    String[] params() default {};

    String cookie() default "";
}
