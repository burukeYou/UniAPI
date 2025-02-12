package com.burukeyou.uniapi.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.Proxy;

/**
 * proxy config annotation
 * @author caizhihao
 */
@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpProxyCfg {

    /**
     * proxy type
     */
    Proxy.Type type() default  Proxy.Type.HTTP;

    /**
     * prosy address
     */
    String address() default "";

    /**
     * proxy auth username
     */
    String username() default "";

    /**
     * proxy auth password
     */
    String password() default "";

}
