package com.burukeyou.demo.spring;

import java.util.Collections;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

@Component
public class MyEnvironmentAware implements EnvironmentAware {


    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment)environment;
        MutablePropertySources propertySources = env.getPropertySources();
        propertySources.addFirst(new MapPropertySource("my-property-source", Collections.singletonMap("bbbq.name", "9999999")));
        System.out.println();
    }
}
