package com.burukeyou.uniapi.queue.config;

import com.burukeyou.uniapi.queue.annotation.QueueApi;
import com.burukeyou.uniapi.config.UniApiRegister;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class QueueApiRegister implements UniApiRegister {

    @Override
    public List<Class<? extends Annotation>> register() {
        return Collections.singletonList(QueueApi.class);
    }
}
