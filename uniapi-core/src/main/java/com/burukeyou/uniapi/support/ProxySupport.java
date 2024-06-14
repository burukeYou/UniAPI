package com.burukeyou.uniapi.support;

import lombok.Data;
import org.springframework.core.env.Environment;

@Data
public class ProxySupport {

    private Class<?> targetClass;

    private Environment environment;

    public ProxySupport(Environment environment) {
        this.environment = environment;
    }
}
