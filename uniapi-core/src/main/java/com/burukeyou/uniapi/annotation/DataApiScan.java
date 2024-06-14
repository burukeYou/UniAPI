package com.burukeyou.uniapi.annotation;

import com.burukeyou.uniapi.config.DataApiStarterConfiguration;
import com.burukeyou.uniapi.register.DataApiBeanCustomRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DataApiStarterConfiguration.class,DataApiBeanCustomRegister.class})
public @interface DataApiScan {

    String[] value();
}
