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
     *  接口url
     */
    String url() default "";

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
    RequestMethod method() default RequestMethod.GET;

    /**
     *  请求头
     */
    String[] headers() default {};

    /**
     * 请求参数
     */
    String[] params() default {};

    /**
     *  请求Cookie
     */
    String cookie() default "";
}
