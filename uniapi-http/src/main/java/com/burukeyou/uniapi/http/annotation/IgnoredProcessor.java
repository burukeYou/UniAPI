package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import org.springframework.core.annotation.AliasFor;

/**
 * Whether to ignore {@link HttpApiProcessor} or the execution of HttpApiProcessor's method
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoredProcessor {

    /**
     * Whether to ignore all the method of {@link HttpApiProcessor}
     */
    @AliasFor("ignoreAll")
    boolean value() default false;

    /**
     * Whether to ignore all the method of {@link HttpApiProcessor}
     */
    @AliasFor("value")
    boolean ignoreAll() default false;

    /**
     * The method name of {@link HttpApiProcessor} that needs to be ignored
     */
    String[] ignoreMethods() default {};
}
