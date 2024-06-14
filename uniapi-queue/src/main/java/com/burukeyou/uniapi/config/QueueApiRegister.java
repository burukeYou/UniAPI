package com.burukeyou.uniapi.config;

import com.burukeyou.uniapi.annotation.QueueApi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class QueueApiRegister implements UniApiRegister {

    @Override
    public List<Class<? extends Annotation>> register() {
        return Collections.singletonList(QueueApi.class);
    }
}
