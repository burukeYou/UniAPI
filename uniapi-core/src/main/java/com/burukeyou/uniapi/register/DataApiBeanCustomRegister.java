package com.burukeyou.uniapi.register;

import com.burukeyou.uniapi.annotation.UniAPIScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 通过DataApiScan注册Bean定义
 * @author caizhihao
 */
public class DataApiBeanCustomRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(UniAPIScan.class.getName());
        if (annotationAttributes == null){
            return;
        }
        String[] values = (String[])annotationAttributes.get("value");
        DataApiScanner scanner = new DataApiScanner(registry);
        scanner.doScan(values);
    }
}
