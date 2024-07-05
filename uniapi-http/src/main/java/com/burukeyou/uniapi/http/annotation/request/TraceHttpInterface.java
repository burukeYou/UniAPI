package com.burukeyou.uniapi.http.annotation.request;

import com.burukeyou.uniapi.http.support.RequestMethod;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HttpInterface(method = RequestMethod.TRACE)
public @interface TraceHttpInterface {

    
}
