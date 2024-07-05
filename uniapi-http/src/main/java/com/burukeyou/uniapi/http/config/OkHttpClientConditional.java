package com.burukeyou.uniapi.http.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OkHttpClientConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        try {
            beanFactory.getBean(OkHttpClient.class);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }
        return false;
    }
}
