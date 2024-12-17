package com.burukeyou.uniapi.http.config;

import com.burukeyou.uniapi.http.extension.client.GlobalOkHttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class OkHttpClientFactoryConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        try {
             beanFactory.getBean(GlobalOkHttpClientFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }
        return false;
    }
}
