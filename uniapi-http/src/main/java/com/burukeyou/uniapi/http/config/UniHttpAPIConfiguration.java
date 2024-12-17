package com.burukeyou.uniapi.http.config;

import com.burukeyou.uniapi.http.extension.client.DefaultGlobalOkHttpClientFactory;
import com.burukeyou.uniapi.http.extension.client.GlobalOkHttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniHttpAPIConfiguration {

    @Bean("uniHttpDefaultGlobalOkHttpClientFactory")
    @Conditional(OkHttpClientFactoryConditional.class)
    public GlobalOkHttpClientFactory okHttpClient(){
        return new DefaultGlobalOkHttpClientFactory();
    }

}
