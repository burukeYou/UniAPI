package com.burukeyou.uniapi.http.config;

import com.burukeyou.uniapi.http.extension.DefaultOkHttpClientFactory;
import com.burukeyou.uniapi.http.extension.OkHttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniHttpAPIConfiguration {

    @Bean("UniHttpOkHttpClientFactory")
    @Conditional(OkHttpClientFactoryConditional.class)
    public OkHttpClientFactory okHttpClient(){
        return new DefaultOkHttpClientFactory();
    }

}
