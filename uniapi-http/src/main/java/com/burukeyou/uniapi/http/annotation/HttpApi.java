package com.burukeyou.uniapi.http.annotation;


import com.burukeyou.uniapi.http.extension.DefaultHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;

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
