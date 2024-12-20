package com.burukeyou.uniapi.http.core.channel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.param.ComposePar;
import com.burukeyou.uniapi.http.annotation.param.CookiePar;
import com.burukeyou.uniapi.http.annotation.param.HeaderPar;
import com.burukeyou.uniapi.http.annotation.param.PathPar;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.core.conveter.request.HttpRequestBodyConverter;
import com.burukeyou.uniapi.http.core.conveter.request.HttpRequestBodyConverterChain;
import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.request.HttpBody;
import com.burukeyou.uniapi.http.core.request.HttpBodyBinary;
import com.burukeyou.uniapi.http.core.request.HttpBodyFormData;
import com.burukeyou.uniapi.http.core.request.HttpBodyJSON;
import com.burukeyou.uniapi.http.core.request.HttpBodyMultipart;
import com.burukeyou.uniapi.http.core.request.HttpMetadata;
import com.burukeyou.uniapi.http.core.request.HttpUrl;
import com.burukeyou.uniapi.http.core.request.MultipartDataItem;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.ClassFieldArgList;
import com.burukeyou.uniapi.support.arg.MapArgList;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.ListsUtil;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/**
 * @author caizhihao
 */
@Getter
@Setter
public abstract class AbstractHttpMetadataParamFinder extends AbstractInvokeCache implements HttpMetadataFinder {

    protected HttpApi api;
    protected HttpInterface httpInterface;

    // todo 升级为支持实时更新的环境变量上下文
    protected Environment environment;

    protected  MethodInvocation methodInvocation;

    private HttpRequestBodyConverter converterChain;

    protected static final String HEADER_CONTENT_TYPE = "Content-Type";

    public AbstractHttpMetadataParamFinder(HttpApi api,
                                           HttpInterface httpInterface,
                                           Environment environment) {
        this.api = api;
        this.httpInterface = httpInterface;
        this.environment = environment;
        this.converterChain = new HttpRequestBodyConverterChain(this).getChain();
    }

    @Override
    public HttpMetadata find(Method method, Object[] args) {
        HttpUrl httpUrl = HttpUrl.builder()
                .path(httpInterface.path())
                .build();

        if (StringUtils.isNotBlank(httpInterface.url())){
            httpUrl.setUrl(getEnvironmentValue(httpInterface.url()));
        }else {
            httpUrl.setUrl(getEnvironmentValue(api.url()));
        }

        HttpMetadata httpMetadata = new HttpMetadata();
        httpMetadata.setRequestMethod(httpInterface.method());
        httpMetadata.setHttpUrl(httpUrl);

        MethodArgList argList = new MethodArgList(method, args);
        fillHttpMetadata(httpMetadata,argList);
        parseCombineParam(httpMetadata, argList);
        initBase(httpMetadata);
        return httpMetadata;
    }

    private void initBase(HttpMetadata httpMetadata) {
        if (httpMetadata.getBody() != null){
            httpMetadata.setContentType(httpMetadata.getBody().getContentType());
        }
        if (StringUtils.isNotBlank(httpInterface.contentType())){
            httpMetadata.setContentType(httpInterface.contentType().trim());
        }
        if (StringUtils.isBlank(httpMetadata.getContentType()) && httpMetadata.getRequestMethod().needBody()){
            httpMetadata.setContentType(MediaTypeEnum.APPLICATION_JSON.getType());
        }
    }

    public <T> T getEnvironmentValue(T value){
        if (value == null){
            return null;
        }
        if(value.getClass() != String.class){
            return value;
        }
        return (T)environment.resolvePlaceholders(value.toString());
    }

    public <T> List<T> getEnvironmentValueList(T[] value){
        if (value == null || value.length == 0){
            return  Collections.emptyList();
        }
        for (int i = 0; i < value.length; i++) {
            value[i] = getEnvironmentValue(value[i]);
        }
        return Arrays.asList(value);
    }


    private List<Cookie> findCookies(ArgList argList) {
        List<Cookie> cookies = new ArrayList<>(parseCookie(getEnvironmentValue(httpInterface.cookie())));
        for (Param param : argList) {
            Object argValue = param.getValue();
            if (param.isValueNotExist()){
                continue;
            }

            CookiePar annotation = param.getAnnotation(CookiePar.class);
            if (annotation == null){
                continue;
            }

            if (argValue instanceof Cookie){
                cookies.add((Cookie)argValue);
                continue;
            }

            if (param.isCollection(Cookie.class)){
                List<Cookie> tmp = param.castListValue(Cookie.class);
                if (!CollectionUtils.isEmpty(tmp)){
                    cookies.addAll(tmp);
                }
                continue;
            }

            if (Map.class.equals(param.getType())){
                List<Cookie> tmpList = ((Map<?, ?>) argValue).entrySet().stream()
                        .map(e -> new Cookie(e.getKey().toString(), e.getValue().toString()))
                        .collect(Collectors.toList());
                cookies.addAll(tmpList);
                continue;
            }

            if (param.isObject()){
                List<Cookie> tmpList = JSON.parseObject(JSON.toJSONString(argValue)).entrySet().stream()
                        .map(e -> new Cookie(e.getKey(), e.getValue().toString()))
                        .collect(Collectors.toList());
                cookies.addAll(tmpList);
                continue;
            }

            if (String.class.equals(param.getType())){
                if (StringUtils.isNotBlank(annotation.value())){
                    // 指定了name 当成单个cookie处理
                    cookies.add(new Cookie(annotation.value(),argValue.toString()));
                }else {
                    cookies.addAll(parseCookie(argValue.toString()));
                }
                continue;
            }
        }

        return cookies;
    }

    public List<Cookie> parseCookie(String cookie){
        if (StringUtils.isBlank(cookie)){
            return Collections.emptyList();
        }
        List<Cookie> cookieList = new ArrayList<>();
        for (String item : cookie.split(";")) {
            String[] split = item.split("=");
            cookieList.add(new Cookie(split[0].trim(),split[1].trim()));
        }
        return cookieList;
    }

    public HttpMetadata find(MethodInvocation methodInvocation){
        Method method = methodInvocation.getMethod();
        Object[] args = methodInvocation.getArguments();
        return find(method,args);
    }

    @Override
    public Map<String, Object> findQueryParam(Method method, Object[] args) {
        return findQueryParam(new MethodArgList(method,args));
    }

    @Override
    public Map<String, String> findPathParam(Method method, Object[] args) {
        return findPathParam(new MethodArgList(method,args));
    }

    @Override
    public Map<String, String> findHeaders(Method method, Object[] args) {
        return findHeaders(new MethodArgList(method,args));
    }

    @Override
    public HttpBody findHttpBody(Method method, Object[] args) {
        return findHttpBody(new MethodArgList(method,args));
    }

    public HttpBody findHttpBody(ArgList argList){
        List<HttpBody> bodyList = new ArrayList<>();
        for (Param methodArg : argList) {
            if (methodArg.isValueNotExist()){
                continue;
            }
            HttpBody httpBody = converterChain.convert(methodArg);
            if (httpBody != null){
                bodyList.add(httpBody);
            }
        }

        if (CollectionUtils.isEmpty(bodyList)){
            return null;
        }
        if (bodyList.size() == 1){
            return bodyList.get(0);
        }

        // HttpBodyBinary、HttpBodyFormData、HttpBodyJSON、HttpBodyMultipart
        Map<? extends Class<? extends HttpBody>, List<HttpBody>> map = bodyList.stream().collect(Collectors.groupingBy(HttpBody::getClass));
        if (map.size() > 1){
            String msg = bodyList.stream().map(HttpBody::getContentType).collect(Collectors.joining(","));
            throw new BaseUniHttpException("only one request body can be set, but it was found that there are " + msg);
        }

        // combine body
        for (Map.Entry<? extends Class<? extends HttpBody>, List<HttpBody>> entry : map.entrySet()) {
            Class<? extends HttpBody> bodyClass = entry.getKey();
            List<HttpBody> list = entry.getValue();

            if (bodyClass == HttpBodyBinary.class || bodyClass == HttpBodyJSON.class){
                throw new BaseUniHttpException("Cannot specify multiple @BodyBinaryPar or @BodyJsonPar");
            }

            if (bodyClass.equals(HttpBodyFormData.class)){
                Map<String,String> formData = new HashMap<>();
                list.forEach(e -> formData.putAll(((HttpBodyFormData)e).getFormData()));
                return new HttpBodyFormData(formData);
            }

            if (bodyClass.equals(HttpBodyMultipart.class)){
                List<MultipartDataItem> allItemList = list.stream().flatMap(e -> ((HttpBodyMultipart) e).getMultiPartData().stream()).collect(Collectors.toList());
                return new HttpBodyMultipart(allItemList);
            }
        }

        throw new BaseUniHttpException("http body build exception can not combine body");
    }


    public Map<String, String> objToMap(Object argValue) {
        return JSON.parseObject(JSON.toJSONString(argValue), new TypeReference<Map<String, String>>() {});
    }


    public Map<String, String> findHeaders(ArgList argList) {
        String[] headers = httpInterface.headers();
        Map<String, String> fixHeaders = Arrays.stream(headers)
                .filter(e -> e.contains("=") || e.contains(":"))
                .collect(Collectors.toMap(e -> e.split("[=:]")[0].trim(), e -> e.split("[=:]")[1].trim()));

        for (Param methodArg : argList) {
            HeaderPar annotation = methodArg.getAnnotation(HeaderPar.class);
            if (annotation == null || methodArg.isCollection()){
                continue;
            }

            Object argValue = methodArg.getValue();
            String tmpFiledName = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : methodArg.getName();
            boolean isObjFlag = isObjOrMap(methodArg.getType());
            if (StringUtils.isBlank(tmpFiledName) && !isObjFlag){
                throw new IllegalArgumentException("use @HeaderPar please specify parameter name");
            }

            Object value = getActualArgValue(argValue);
            if (value == null){
                if (!isObjFlag){
                    fixHeaders.put(tmpFiledName,null);
                }
                // 为对象直接忽略
                continue;
            }

            if (!isObjFlag){
                fixHeaders.put(tmpFiledName, getArgFillValue(argValue).toString());
            }else {
                fixHeaders.putAll(getHeaderParamForObj(value));
            }

        }

        if (StringUtils.isNotBlank(httpInterface.contentType())){
            fixHeaders.put(HEADER_CONTENT_TYPE,httpInterface.contentType());
        }
        return fixHeaders;
    }

    public Map<String,String> getHeaderParamForObj(Object obj){
        Map<String,String> map = new HashMap<>();
        for (Param param : autoGetArgList(obj)) {
            Object fieldValue = getArgFillValue(param.getValue());
            String fileName = param.getName();

            if (fieldValue != null && isObjOrMap(param.getType())){
                fieldValue = null;
            }
            HeaderPar annotation = param.getAnnotation(HeaderPar.class);
            if (annotation != null){
                fileName = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : fileName;
            }
            map.put(fileName,fieldValue == null ? null : fieldValue.toString());
        }
        return map;
    }

    public Map<String,Object> findQueryParam(ArgList argList) {
        String[] params = httpInterface.params();
        Map<String, String> queryParam = Arrays.stream(params)
                .filter(e-> e.contains("=") || e.contains(":"))
                .collect(Collectors.toMap(e -> e.split("[=:]")[0], e -> e.split("[=:]")[1]));

        String paramStr = httpInterface.paramStr();
        if (StringUtils.isNotBlank(paramStr) && paramStr.contains("=")){
            for (String item  :paramStr.split("[&;；]")){
                String[] itemArr = item.split("=");
                queryParam.put(itemArr[0],itemArr[1]);
            }
        }

        Map<String,Object> queryMap = new HashMap<>(queryParam);
        for (Param param : argList) {
            QueryPar annotation = param.getAnnotation(QueryPar.class);
            if (annotation == null){
                continue;
            }

            String tmpFiledName = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : param.getName();
            boolean needFlag = isObjOrMap(param.getType());
            if ((!needFlag || param.isCollection()) && StringUtils.isBlank(tmpFiledName)){
                throw new IllegalArgumentException("use @QueryPar please specify parameter name");
            }

            Object argValue = param.getValue();
            Object value = getActualArgValue(argValue);
            if (value == null){
                if (!needFlag){
                    queryMap.put(tmpFiledName,null);
                }
                // 为复杂对象直接忽略
                continue;
            }

            if (!needFlag){
                if (param.isCollection()){
                    value = param.castListValue(Object.class)
                            .stream()
                            .map(Object::toString)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                }
                queryMap.put(tmpFiledName, value);
            }else {
                queryMap.putAll(getQueryParamForObj(value));
            }
        }

        return queryMap;
    }

    public Map<String,String> findPathParam(ArgList argList) {
        Map<String,String> queryMap = new HashMap<>();
        for (Param methodArg : argList) {
            PathPar annotation = methodArg.getAnnotation(PathPar.class);
            if (annotation == null || !methodArg.isNormalValue()){
                continue;
            }

            String tmpFiledName = annotation.value();
            Object value = getActualArgValue(methodArg.getValue());
            if (value == null){
                queryMap.put(tmpFiledName,null);
            }else {
                queryMap.put(tmpFiledName,value.toString());
            }
        }
        return queryMap;
    }


    public ArgList autoGetArgList(Object obj){
        if (obj == null){
            return ListsUtil.emptyArgList();
        }

        if (!isObjOrMap(obj.getClass())){
            return ListsUtil.emptyArgList();
        }

        ArgList argList;
        if (Map.class.isAssignableFrom(obj.getClass())){
            argList = new MapArgList((Map<?,?>)obj);
        }else {
            argList = new ClassFieldArgList(obj);
        }
        return argList;
    }

    public Map<String,Object> getQueryParamForObj(Object obj){
        Map<String,Object> map = new HashMap<>();
        for (Param param : autoGetArgList(obj)) {
            Object fieldValue = getArgFillValue(param.getValue());
            String fileName = param.getName();

            if (fieldValue != null && isObjOrMap(param.getType())){
                fieldValue = null;
            }

            QueryPar annotation = param.getAnnotation(QueryPar.class);
            if (annotation != null){
                fileName = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : fileName;
            }
            map.put(fileName,fieldValue);
        }
        return map;
    }


    public void parseCombineParam(HttpMetadata httpMetadata, ArgList list) {
        for (Param methodArg : list) {
            if (methodArg.getValue() == null){
                continue;
            }
            ComposePar annotation = methodArg.getAnnotation(ComposePar.class);
            if (annotation == null || methodArg.isCollection() || !methodArg.isObject()){
                continue;
            }
            fillHttpMetadata(httpMetadata, new ClassFieldArgList(methodArg.getValue()));
        }
    }

    private void fillHttpMetadata(HttpMetadata httpMetadata, ArgList paramArgList) {
        httpMetadata.putQueryParams(findQueryParam(paramArgList));
        httpMetadata.putPathParams(findPathParam(paramArgList));
        httpMetadata.setBodyIfAbsent(findHttpBody(paramArgList));
        httpMetadata.putHeaders(findHeaders(paramArgList));
        httpMetadata.addCookiesList(findCookies(paramArgList));
    }

    public boolean isEmpty(Object arg){
        Object argValue = getActualArgValue(arg);
        if (argValue == null){
            return true;
        }
        if (argValue.getClass() == String.class){
           return StringUtils.isBlank((String)argValue);
        }
        return false;
    }


    public Object getActualArgValue(Object argValue) {
        if (argValue == null) {
            return null;
        }
        Class<?> type = argValue.getClass();
        // 支持枚举参数
        if (type.isEnum()) {
            return  argValue.toString();
        }
        return argValue;
    }


    public  boolean isObjOrMap(Class<?> valueClass){
        ClassLoader classLoader = valueClass.getClassLoader();
        if (valueClass.isPrimitive() || valueClass.isEnum()){
            return false;
        }
        if (valueClass.isArray() || List.class.isAssignableFrom(valueClass) || Collection.class.isAssignableFrom(valueClass)){
            return false;
        }
        if (classLoader == this.getClass().getClassLoader()){
            return true;
        }

        if (Map.class.isAssignableFrom(valueClass)){
            return true;
        }
        return false;
    }

    public  boolean isArr(Class<?> valueClass){
        if (valueClass.isArray() || Collection.class.isAssignableFrom(valueClass)){
            return true;
        }
        return true;
    }

    public  boolean isObjOrArr(Class<?> valueClass){
        if (isObjOrMap(valueClass)){
            return true;
        }

        if (valueClass.isArray() || List.class.isAssignableFrom(valueClass) || Collection.class.isAssignableFrom(valueClass)){
            return true;
        }
        return false;
    }

    public  boolean isObjOrArrOrMap(Class<?> valueClass){
        return isObjOrArr(valueClass) || isObjOrMap(valueClass);
    }


    public Object getArgFillValue(Object argValue) {
        Object value = getActualArgValue(argValue);
        if (value == null){
            return null;
        }
        if (isObjOrArr(argValue.getClass()) || Map.class.isAssignableFrom(argValue.getClass())){
           return JSON.toJSONString(value);
        }
        return value;
    }
}
