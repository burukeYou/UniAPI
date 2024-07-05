package com.burukeyou.uniapi.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@Primary
public class SpringBeanContext implements ApplicationContextAware {

    protected static ApplicationContext springContext;

    public static <T> T getBean(Class<T> clz) {
        try {
            return springContext.getBean(clz);
        } catch (BeansException e) {
           return null;
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springContext =  applicationContext;
    }
}
