package com.burukeyou.uniapi.http.annotation.request;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.RequestMethod;
import org.springframework.core.annotation.AliasFor;

/** Http interface configuration
 *
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInterface {

    /**
     *  Request the root address of the interface, support get from environment variable
     */
    String url() default "";

    /**
     * Interface path, support get from environment variable
     */
    @AliasFor("value")
    String path() default "";

    /**
     * Interface path
     */
    @AliasFor("path")
    String value() default "";

    /**
     *  Request method
     */
    RequestMethod method() default RequestMethod.GET;

    /**
     *  Request header, a key-value pair , the value supports get from environment variables
     */
    String[] headers() default {};

    /**
     *  request body contentType
     */
    String contentType() default "";

    /**
     * Query parameters,a key-value pair , the value supports get from environment variables
     */
    String[] params() default {};

    /**
     *  The complete string of query parameters
     *      
     */
    String paramStr() default "";

    /**
     * Request the complete string of the cookie
     *          
     */
    String cookie() default "";

    /**
     *  Specify extension points for custom HTTP requests during execution
     *       please see {@link HttpApiProcessor}
     */
    Class<? extends HttpApiProcessor<? extends Annotation>>[] processor() default {};

    /**
     * Whether to enable async requests
     */
    boolean async() default false;
}
