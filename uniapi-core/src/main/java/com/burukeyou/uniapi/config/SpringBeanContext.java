package com.burukeyou.uniapi.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Component
@Primary
public class SpringBeanContext implements ApplicationContextAware {

    protected static ApplicationContext springContext;

    public static <T> T getBean(Class<T> clz) {
        try {
            return springContext.getBean(clz);
        } catch (NoSuchBeanDefinitionException e) {
           return null;
        }
    }

    public static <T> List<T> listBean(Class<T> clz){
        Map<String, T> map = springContext.getBeansOfType(clz);
        if (map.isEmpty()){
            return Collections.emptyList();
        }
        return new ArrayList<>(map.values());
    }

    public static Object getMultiBean(Class<?> fieldType) {
        Map<String, ?> beansOfType = springContext.getBeansOfType(fieldType);
        if (beansOfType.size() <= 0){
           return null;
        }

        ArrayList<?> objects = new ArrayList<>(beansOfType.values());
        if (objects.size() == 1){
            return objects.get(0);
        }

        // 有多个Bean实现，尝试获取与该类型类名相同的类
        for (Object value : objects) {
            if (isSameClass(value.getClass(), fieldType)) {
                return value;
            }
        }
        return null;
    }


    public static boolean isSameClass(Class<?> class1, Class<?> class2) {
        if (class1 == class2) {
            return true;
        }
        if (class2.isAssignableFrom(class1) || class1.isAssignableFrom(class2)) {
            return false;
        }
        return false;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springContext =  applicationContext;
    }
}
