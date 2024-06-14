package com.burukeyou.demo.annotation;


import com.burukeyou.demo.config.MeituanApiChannelHandler;
import com.burukeyou.uniapi.annotation.HttpApi;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author caizhihao
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HttpApi(processor = MeituanApiChannelHandler.class)
public @interface MetuanDataApi {

    @AliasFor(annotation = HttpApi.class)
    String url() default "${channel.meituan.url}";

    String name() default "sb";

    String id() default "sb";

}
