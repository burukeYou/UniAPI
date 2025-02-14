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

/**
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpInterface(method = RequestMethod.DELETE)
public @interface DeleteHttpInterface {

    /**
     *  Request the root address of the interface
     */
    @AliasFor(annotation = HttpInterface.class)
    String url() default "";

    /**
     * Interface path
     */
    @AliasFor(annotation = HttpInterface.class)
    String path() default "";

    /**
     * Interface path
     */
    @AliasFor(annotation = HttpInterface.class)
    String value() default "";

    /**
     *  Request header
     */
    @AliasFor(annotation = HttpInterface.class)
    String[] headers() default {};

    /**
     * Query parameters
     */
    @AliasFor(annotation = HttpInterface.class)
    String[] params() default {};

    /**
     *  The complete string of query parameters
     *      
     */
    @AliasFor(annotation = HttpInterface.class)
    String paramStr() default "";

    /**
     * Request the complete string of the cookie
     *          such as a=1;b=2;c=3
     */
    @AliasFor(annotation = HttpInterface.class)
    String cookie() default "";

    /**
     *  Specify extension points for custom HTTP requests during execution
     *       please see {@link HttpApiProcessor}
     */
    @AliasFor(annotation = HttpInterface.class)
    Class<? extends HttpApiProcessor<? extends Annotation>>[] processor() default {};

    /**
     * Whether to enable async requests
     */
    @AliasFor(annotation = HttpInterface.class)
    boolean async() default false;
}
