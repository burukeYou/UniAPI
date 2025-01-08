package com.burukeyou.uniapi.http.core.proxy;

import java.lang.reflect.Method;

import com.burukeyou.uniapi.config.SpringBeanContext;
import com.burukeyou.uniapi.core.proxy.AbstractAnnotationInvokeProxy;
import com.burukeyou.uniapi.exception.BaseUniApiException;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.channel.DefaultHttpApiInvoker;
import com.burukeyou.uniapi.http.core.serialize.json.JsonSerializeConverter;
import com.burukeyou.uniapi.http.core.serialize.xml.XmlSerializeConverter;
import com.burukeyou.uniapi.http.extension.client.GlobalOkHttpClientFactory;
import com.burukeyou.uniapi.http.extension.client.OkHttpClientFactory;
import com.burukeyou.uniapi.http.support.HttpApiAnnotationMeta;
import com.burukeyou.uniapi.http.utils.BizUtil;
import okhttp3.OkHttpClient;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author caizhihao
 */
public class HttpApiAnnotationProxy  extends AbstractAnnotationInvokeProxy<HttpApiAnnotationMeta> {

    private final OkHttpClient httpClient;

    private final XmlSerializeConverter xmlSerializeConverter;

    private final JsonSerializeConverter jsonSerializeConverter;

    public HttpApiAnnotationProxy(HttpApiAnnotationMeta annotationMeta) {
        super(annotationMeta);
        httpClient = initHttpClient(annotationMeta);
        xmlSerializeConverter = initXmlSerializeConverter(annotationMeta);
        jsonSerializeConverter = initJsonSerializeConverter(annotationMeta);
    }


    @Override
    public Object invoke(Class<?> targetClass,MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        if (annotationMeta.getHttpApi() != null){
            HttpInterface httpInterface = AnnotatedElementUtils.getMergedAnnotation(method, HttpInterface.class);
            if (httpInterface != null){
                return new DefaultHttpApiInvoker(annotationMeta,targetClass,httpInterface,methodInvocation,httpClient,xmlSerializeConverter,jsonSerializeConverter).invoke();
            }
        }
        return null;
    }

    private OkHttpClient initHttpClient(HttpApiAnnotationMeta annotationMeta) {
        OkHttpClientFactory okHttpClientFactory = null;
        Class<? extends OkHttpClientFactory> configHttpClientClass = getHttpClientClass(annotationMeta.getHttpApi());
        if (configHttpClientClass != null){
            okHttpClientFactory = SpringBeanContext.getBean(configHttpClientClass);
            if (okHttpClientFactory == null){
                throw new BaseUniApiException("Unable to find "+ configHttpClientClass.getSimpleName() + " configured with @ httpApi from spring context");
            }
        }else {
            // 如果没配置，从SpringContext获取全局
            okHttpClientFactory = SpringBeanContext.getBean(GlobalOkHttpClientFactory.class);
            if (okHttpClientFactory == null){
                throw new BaseUniApiException("can not find GlobalOkHttpClientFactory from spring context");
            }
        }
        OkHttpClient client = okHttpClientFactory.getHttpClient();
        if (client == null){
            throw new BaseUniApiException(okHttpClientFactory.getClass().getSimpleName() + " getHttpClient() can not return null");
        }
        return client;
    }

    public  Class<? extends OkHttpClientFactory> getHttpClientClass(HttpApi api){
        if(api.httpClient().length > 0){
            return api.httpClient()[0];
        }
        return null;
    }

    private XmlSerializeConverter initXmlSerializeConverter(HttpApiAnnotationMeta annotationMeta) {
        Class<? extends XmlSerializeConverter> xmlConverterClass = annotationMeta.getHttpApi().xmlConverter();
        return BizUtil.getBeanOrNew(xmlConverterClass);
    }

    private JsonSerializeConverter initJsonSerializeConverter(HttpApiAnnotationMeta annotationMeta) {
        Class<? extends JsonSerializeConverter> jsonConverterClass = annotationMeta.getHttpApi().jsonConverter();
        return BizUtil.getBeanOrNew(jsonConverterClass);
    }
}
