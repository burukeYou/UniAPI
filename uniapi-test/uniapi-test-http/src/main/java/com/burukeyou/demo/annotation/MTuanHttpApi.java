package com.burukeyou.demo.annotation;


import com.burukeyou.demo.config.MTuanChannelHandler;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author caizhihao
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HttpApi(processor = MTuanChannelHandler.class)
public @interface MTuanHttpApi {

    @AliasFor(annotation = HttpApi.class)
    String url() default "${channel.mtuan.url}";

    /**
     * 渠道方分配的appId
     */
    String appId() default "${channel.mtuan.appId}";

}
