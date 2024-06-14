package com.burukeyou.uniapi.http.config;

import com.burukeyou.uniapi.config.UniApiRegister;
import com.burukeyou.uniapi.http.annotation.HttpApi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class HttpApiRegister implements UniApiRegister {

    @Override
    public List<Class<? extends Annotation>> register() {
        return Collections.singletonList(HttpApi.class);
    }
}
