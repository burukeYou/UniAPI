package com.burukeyou.uniapi.core.channel;

import com.burukeyou.uniapi.annotation.QueueApi;
import com.burukeyou.uniapi.annotation.QueueInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

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



    private Object handlerForQueue(Method method, Object[] args, QueueInterface queueInterface) {
 /*       RabbitTemplate sender = SpringContextHolder.getBean(RabbitTemplate.class);


        String host = sender.getConnectionFactory().getHost();
        String virtualHost = sender.getConnectionFactory().getVirtualHost();

        String orgName = environment.resolvePlaceholders(rpaDataApi.org());
        String queueName = environment.resolvePlaceholders(queueInterface.queue());
        Object reqData = args[0];
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(reqData));
        jsonObject.putIfAbsent("business",queueInterface.key());
        jsonObject.putIfAbsent(ORG_NAME,orgName);
        jsonObject.putIfAbsent("seq_no","OPSS" + IdWorker.getIdStr());

        String msg = jsonObject.toString();
        try {
            sender.convertAndSend(queueName, msg);
            log.info("DataApi发送消息到队列成功 host:{} virtualHost:{} 队列: {}，消息内容：{}", host,virtualHost,queueName, msg);
        } catch (Exception e) {
            log.info("DataApi发送消息到队列异常 队列: {}，消息内容：{}", queueName, msg,e);
        }*/
        return null;
    }
}
