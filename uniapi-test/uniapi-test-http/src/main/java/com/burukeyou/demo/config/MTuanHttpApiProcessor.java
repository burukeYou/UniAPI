package com.burukeyou.demo.config;

import com.burukeyou.demo.annotation.MTuanHttpApi;
import com.burukeyou.demo.api.WeatherServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.channel.HttpSender;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.core.response.UniHttpResponse;
import com.burukeyou.uniapi.http.extension.processor.HttpApiProcessor;
import com.burukeyou.uniapi.http.support.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MTuanHttpApiProcessor implements HttpApiProcessor<MTuanHttpApi> {

    /**
     *  渠道方分配的公钥
     */
    @Value("${channel.mtuan.publicKey}")
    private String publicKey;

    @Value("${channel.mtuan.appId}")
    private String appId;

    @Autowired
    private Environment environment;

    @Autowired
    private WeatherServiceApi weatherApi;

    /** 实现-postBeforeHttpMetadata： 发送Http请求之前会回调该方法，可对Http请求体的内容进行二次处理
     *
     * @param uniHttpRequest              原来的请求体
     * @param methodInvocation          被代理的方法
     * @return                          新的请求体
     */
    @Override
    public UniHttpRequest postBeforeHttpRequest(UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<MTuanHttpApi> methodInvocation) {
        /**
         * 在查询参数中添加提供的appId字段
         */
        // 获取MTuanHttpApi注解
        MTuanHttpApi apiAnnotation = methodInvocation.getProxyApiAnnotation();

        // 获取MTuanHttpApi注解的appId，由于该appId是环境变量所以我们从environment中解析取出来
        String appIdVar = apiAnnotation.appId();
        appIdVar = environment.resolvePlaceholders(appIdVar);

        // 添加到查询参数中
        uniHttpRequest.putQueryParam("appId",appIdVar);

        /**
         *  生成签名sign字段
         */
        // 获取所有查询参数
        Map<String, Object> queryParam = uniHttpRequest.getHttpUrl().getQueryParam();

        // 获取请求体参数
        HttpBody body = uniHttpRequest.getBody();

        // 生成签名
        String signKey = createSignKey(queryParam,body);

        // 将签名添加到请求头中
        uniHttpRequest.putHeader("sign",signKey);

        return uniHttpRequest;
    }

    private String createSignKey(Map<String, Object> queryParam, HttpBody body) {
        // todo 伪代码
        // 1、将查询参数拼接成字符串
        String queryParamString = queryParam.entrySet()
                .stream().map(e -> e.getKey() + "="+e.getValue())
                .collect(Collectors.joining(";"));

        // 2、将请求体参数拼接成字符串
        String bodyString = "";
        if (body instanceof HttpBodyJSON){
            // application/json  类型的请求体
            bodyString = body.toStringBody();
        }else if (body instanceof HttpBodyFormData){
            // application/x-www-form-urlencoded 类型的请求体
            bodyString = body.toStringBody();
        }else if (body instanceof HttpBodyMultipart){
            // multipart/form-data 类型的请求体
            bodyString =  body.toStringBody();
        }

        // 使用公钥publicKey 加密拼接起来
        String sign = publicKey + queryParamString + bodyString + "-9999";
        return sign;
    }

    private static ThreadLocal<Boolean> flagLocal = new ThreadLocal<>();

    /**
     *  实现-postBeforeHttpMetadata： 发送Http请求时，可定义发送请求的行为 或者打印请求和响应日志。
     */
    @Override
    public UniHttpResponse postSendingHttpRequest(HttpSender httpSender, UniHttpRequest uniHttpRequest, HttpApiMethodInvocation<MTuanHttpApi> methodInvocation) {
        //  忽略 weatherApi.getToken的方法回调，否则该方法也会回调此方法会递归死循环。 或者该接口指定自定义的HttpApiProcessor重写postSendingHttpRequest
//        Method getTokenMethod = ReflectionUtils.findMethod(WeatherServiceApi.class, "getToken",String.class,String.class);
//        if (getTokenMethod == null || getTokenMethod.equals(methodInvocation.getMethod())){
//            return httpSender.sendHttpRequest(uniHttpRequest);
//        }

        // 1、动态获取token和sessionId.
        // 这个接口不应该回调这个方法,否则会递归死循环
        HttpResponse<String> httpResponse = weatherApi.getToken(appId, publicKey);

        // 从响应体获取令牌token
        String token = httpResponse.getBodyResult();
        // 从响应头中获取sessionId
        String sessionId = httpResponse.getHeader("sessionId");

        // 把这两个值放到此次的请求cookie中
        uniHttpRequest.addCookie(new Cookie("token",token));
        uniHttpRequest.addCookie(new Cookie("sessionId",sessionId));

        // 使用框架内置实现发送请求
        UniHttpResponse rsp = httpSender.sendHttpRequest(uniHttpRequest);

        log.info("开始发送Http请求 请求响应报文:{}",rsp.toHttpProtocol());

        return rsp;
    }

    /**
     *  实现-postAfterHttpResponseBodyResult： 反序列化后Http响应体的内容后回调，可对该结果进行二次处理返回
     * @param bodyResult                     Http响应体反序列化后的结果
     * @param rsp                            原始Http响应对象
     * @param methodInvocation               被代理的方法
     * @param httpMetadata                   Http请求体
     * @return
     */
    @Override
    public Object postAfterHttpResponseBodyResult(Object bodyResult, UniHttpResponse rsp, HttpApiMethodInvocation<MTuanHttpApi> methodInvocation) {
        if (bodyResult instanceof BaseRsp){
            BaseRsp baseRsp = (BaseRsp) bodyResult;
            // 设置
            baseRsp.setCode(999);
        }

        return bodyResult;
    }
}
