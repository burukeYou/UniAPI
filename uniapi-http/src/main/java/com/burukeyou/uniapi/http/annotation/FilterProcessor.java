package com.burukeyou.uniapi.http.annotation;

import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.ProcessorMethod;

import java.lang.annotation.*;

/**
 * configure whether to call back the method of  {@link HttpApiProcessor}.
 * Sometimes when we use HttpApiProcessor on a class but don't want to use it on certain methods, we can use this annotation to handle it
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterProcessor {

    /**
     * Whether to ignore all the method of {@link HttpApiProcessor}
     */
    boolean ignoreAll() default false;

    /**
     * Whether to ignore  the method of  {@link HttpApiProcessor#postSendingHttpRequest}
     */
    boolean ignoreSending() default false;

    /**
     * The method name of {@link HttpApiProcessor} that needs to be ignored
     */
    ProcessorMethod[] excludeMethods() default {};

    /**
     * The method name of {@link HttpApiProcessor} that needs to  include, other is excluded
     */
    ProcessorMethod[] includeMethods() default {};
}
