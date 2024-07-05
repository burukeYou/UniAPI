package com.burukeyou.uniapi.http.annotation;


import com.burukeyou.uniapi.http.extension.DefaultHttpApiProcessor;
import com.burukeyou.uniapi.http.extension.HttpApiProcessor;

import java.lang.annotation.*;

/**
 * HTTP API configuration
 *
 * @author caizhihao
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {

    /**
     * Configure global HTTP request URL
     *      Support taking values from environmental variables, such as ${xx.url}
     */
    String url() default "";

    /**
     *  Specify extension points for custom HTTP requests during execution
     *       please see {@link HttpApiProcessor}
     */
    Class<? extends HttpApiProcessor<? extends Annotation>> processor() default DefaultHttpApiProcessor.class;
}
