package com.burukeyou.uniapi.register;


import com.burukeyou.uniapi.config.UniApiRegister;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author caizhihao
 */
public class UniApiScanner extends ClassPathBeanDefinitionScanner {

    public UniApiScanner(BeanDefinitionRegistry registry) {
        super(registry,false);
        addDataApiFilter();
    }

    private void addDataApiFilter(){
        ServiceLoader<UniApiRegister> load = ServiceLoader.load(UniApiRegister.class);
        for (UniApiRegister register : load) {
            List<Class<? extends Annotation>> uniApiList = register.register();
            for (Class<? extends Annotation> aClass : uniApiList) {
                addIncludeFilter(new AnnotationTypeFilter(aClass));
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
                beanClass = UniApiScanner.class.getClassLoader().loadClass(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            beanDefinition.setBeanClass(UniApiFactoryBean.class);
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
