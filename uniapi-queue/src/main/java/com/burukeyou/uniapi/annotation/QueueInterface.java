package com.burukeyou.uniapi.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** 消息队列配置
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueueInterface {

    /**
     * 请求队列名
     */
    @AliasFor("queue")
    String value() default "";

    /**
     * 请求队列名
     */
    @AliasFor("value")
    String queue() default "";

    /**
     *  队列接口分类
     */
    String key() default "";

    /**
     *  消息发送器
     * @return
     */
    Class<?> sender();
}
