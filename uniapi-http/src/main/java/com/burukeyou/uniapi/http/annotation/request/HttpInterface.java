package com.burukeyou.uniapi.http.annotation.request;

import com.burukeyou.uniapi.http.extension.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/** Http interface configuration
 *
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInterface {

    /**
     *  Request the root address of the interface
     */
    String url() default "";

    /**
     * Interface path
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
     *  Request header
     */
    String[] headers() default {};

    /**
     *  request body contentType
     */
    String contentType() default "";

    /**
     * Query parameters
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
}
