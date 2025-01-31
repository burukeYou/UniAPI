package com.burukeyou;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.HttpFastRetry;
import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.core.retry.invocation.HttpRetryInvocation;
import com.burukeyou.uniapi.http.core.retry.invocation.ResultInvocation;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryInterceptorPolicy;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryResponsePolicy;
import com.burukeyou.uniapi.http.core.retry.policy.HttpRetryResultPolicy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolicyData {

    public static class Policy1 implements HttpRetryResultPolicy<BaseRsp<String>> {

        @Override
        public boolean canRetry(BaseRsp<String> stringBaseRsp) {
            log.info("Policy1: {}", JSON.toJSONString(stringBaseRsp));
            return stringBaseRsp == null ||  stringBaseRsp.getCode() > 8;
        }
    }

    public static class Policy2 implements HttpRetryResponsePolicy<BaseRsp<String>> {

        private int a = 0;

        @Override
        public boolean canRetry(ResultInvocation<BaseRsp<String>> invocation) {
            a++;
            BaseRsp<String> bodyResult = invocation.getBodyResult();
            UniHttpRequest request = invocation.getRequest();
            UniHttpResponse response = invocation.getResponse();
            long curExecuteCount = invocation.getCurExecuteCount();
            log.info("Policy2: {} 当前执行次数:{} a:{}", JSON.toJSONString(bodyResult), invocation.getCurExecuteCount(),a);
            return bodyResult == null || bodyResult.getCode() > 8;
        }
    }


    public static class Policy3 implements HttpRetryInterceptorPolicy<BaseRsp<String>> {
        @Override
        public boolean beforeExecute(UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
            log.info("before curCount:{}",invocation.getCurExecuteCount());
            HttpFastRetry httpFastRetry = invocation.getHttpFastRetry();
            return HttpRetryInterceptorPolicy.super.beforeExecute(uniHttpRequest, invocation);
        }

        @Override
        public boolean afterExecuteFail(Exception exception, UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
            log.info("after fail curCount:{}",invocation.getCurExecuteCount());
            return HttpRetryInterceptorPolicy.super.afterExecuteFail(exception, uniHttpRequest, invocation);
        }

        @Override
        public boolean afterExecuteSuccess(BaseRsp<String> bodyResult, UniHttpRequest uniHttpRequest, UniHttpResponse uniHttpResponse, HttpRetryInvocation invocation) {
            log.info("after success: {} curCount:{}", JSON.toJSONString(bodyResult),invocation.getCurExecuteCount());
            log.info("aaaa {}",uniHttpResponse.getBodyToString());
            return bodyResult.getCode() > 10;
        }
    }


}
