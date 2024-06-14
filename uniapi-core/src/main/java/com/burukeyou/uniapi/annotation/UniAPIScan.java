package com.burukeyou.uniapi.annotation;

import com.burukeyou.uniapi.config.UniApiStarterConfiguration;
import com.burukeyou.uniapi.register.UniApiBeanCustomRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * @author caizhihao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({UniApiStarterConfiguration.class, UniApiBeanCustomRegister.class})
public @interface UniAPIScan {

    String[] value();
}
