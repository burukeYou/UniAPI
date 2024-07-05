package com.burukeyou.uniapi.http.annotation.request;


import com.burukeyou.uniapi.http.support.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author caizhihao
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpInterface(method = RequestMethod.POST)
public @interface PostHttpInterface {

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
     *      such as 'a=1&b=2&c=3'
     */
    @AliasFor(annotation = HttpInterface.class)
    String paramStr() default "";

    /**
     * Request the complete string of the cookie
     *          such as  'a=1;b=2;c=3'
     */
    @AliasFor(annotation = HttpInterface.class)
    String cookie() default "";

}
