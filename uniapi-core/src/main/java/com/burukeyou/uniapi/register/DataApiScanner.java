package com.burukeyou.uniapi.register;


import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author caizhihao
 */
public class DataApiScanner extends ClassPathBeanDefinitionScanner {

    // todo config
    protected static final List<String> PROXY_ANNOTATIONS = Arrays.asList(
            "com.burukeyou.uniapi.annotation.HttpApi",
            "com.burukeyou.uniapi.annotation.QueueApi");

    public DataApiScanner(BeanDefinitionRegistry registry) {
        super(registry,false);
        addDataApiFilter();
    }

    private void addDataApiFilter(){
        for (String name : PROXY_ANNOTATIONS) {
            try {
                // Class<? extends Annotation> annotation
                Class<?> aClass = Class.forName(name);
                addIncludeFilter(new AnnotationTypeFilter((Class<? extends Annotation>) aClass));
            } catch (ClassNotFoundException e) {
            }
        }
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        // update bean define
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition)holder.getBeanDefinition();
            Class<?> beanClass = null;
            try {
                beanClass = DataApiScanner.class.getClassLoader().loadClass(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            beanDefinition.setBeanClass(DataApiFactoryBean.class);
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinition.setLazyInit(false);
        }
        return beanDefinitionHolders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }


}
