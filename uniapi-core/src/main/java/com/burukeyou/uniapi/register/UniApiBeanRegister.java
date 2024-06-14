package com.burukeyou.uniapi.register;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/** 注册默认的apiPackages下的Bean定义
 * @author caizhihao
 */
public class UniApiBeanRegister implements BeanDefinitionRegistryPostProcessor {

    private String[] apiPackages;

    public UniApiBeanRegister(String[] apiPackages) {
        this.apiPackages = apiPackages;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        new UniApiScanner(registry).doScan(apiPackages);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
