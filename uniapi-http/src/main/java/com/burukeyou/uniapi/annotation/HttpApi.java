package com.burukeyou.uniapi.annotation;


import com.burukeyou.uniapi.extension.DefaultHttpApiProcessor;
import com.burukeyou.uniapi.extension.HttpApiProcessor;

import java.lang.annotation.*;

/**
 * Http API配置
 * @author caizhihao
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {

    String url() default "";

    Class<? extends HttpApiProcessor<? extends Annotation>> processor() default DefaultHttpApiProcessor.class;
}
