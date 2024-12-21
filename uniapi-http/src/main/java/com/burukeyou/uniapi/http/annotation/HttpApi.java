package com.burukeyou.uniapi.http.annotation;


import com.burukeyou.uniapi.http.extension.client.OkHttpClientFactory;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;

import java.lang.annotation.*;

/**
 * HTTP API configuration
 *      suggest config channel-related parameters
 *      The HTTP API annotation of UniAPI can be marked on a class or interface,
 *      indicating that the proxy logic of HTTP API can be applied to the methods of that class or interface,
 *      helping us quickly send and deserialize an HTTP request
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
    Class<? extends HttpApiProcessor<? extends Annotation>> processor()[] default {};

    /**
     *  Config custom http client, if not config will use default http client
     */
    Class<? extends OkHttpClientFactory> httpClient()[] default {};

}
