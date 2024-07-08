package com.burukeyou.uniapi.queue.core.channel;

import com.burukeyou.uniapi.queue.annotation.QueueApi;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class BaseQueueApiInvoker {

    private QueueApi api;

    protected Class<?> targetClass;

    public BaseQueueApiInvoker(QueueApi api, Class<?> targetClass) {
        this.api = api;
        this.targetClass = targetClass;
    }

    public Object invoke(MethodInvocation methodInvocation) {


        return null;
    }

}
