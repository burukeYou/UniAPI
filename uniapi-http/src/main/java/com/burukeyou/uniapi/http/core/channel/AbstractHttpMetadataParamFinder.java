package com.burukeyou.uniapi.http.core.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.annotation.param.*;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.core.request.*;
import com.burukeyou.uniapi.http.annotation.param.*;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.ClassFieldArgList;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caizhihao
 */
@Data
public abstract class AbstractHttpMetadataParamFinder implements HttpMetadataFinder {

    protected HttpApi api;
    protected HttpInterface httpInterface;

    protected Environment environment;

    protected  MethodInvocation methodInvocation;


    public AbstractHttpMetadataParamFinder(HttpApi api,
                                           HttpInterface httpInterface,
                                           Environment environment) {
        this.api = api;
        this.httpInterface = httpInterface;
        this.environment = environment;
    }

    @Override
    public HttpMetadata find(Method method, Object[] args) {
        HttpUrl httpUrl = HttpUrl.builder()
                .url(environment.resolvePlaceholders(api.url()))
                .path(httpInterface.path())
                .build();

        HttpMetadata httpMetadata = new HttpMetadata();
        httpMetadata.setRequestMethod(httpInterface.method());
        httpMetadata.setHttpUrl(httpUrl);

        MethodArgList argList = new MethodArgList(method, args);
        fillHttpMetadata(httpMetadata,argList);
        parseCombineParam(httpMetadata, argList);
        return httpMetadata;
    }

    private List<Cookie> findCookies(ArgList argList) {
        List<Cookie> cookies = new ArrayList<>(parseCookie(httpInterface.cookie()));
        for (Param param : argList) {
            Object argValue = param.getValue();
            if (argValue == null){
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

            CookieString annotation = param.getAnnotation(CookieString.class);
            if (annotation != null){
                if (String.class.equals(param.getType())){
                    cookies.addAll(parseCookie(argValue.toString()));
                }else if (param.isCollection(String.class)){
                    for (String cookieStr : param.castListValue(String.class)) {
                        cookies.addAll(parseCookie(cookieStr));
                    }
                }
                continue;
            }

            CookiePair cookiePairAnno = param.getAnnotation(CookiePair.class);
            if (cookiePairAnno != null){
                cookies.add(new Cookie(cookiePairAnno.value(),argValue.toString()));
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



   /* private HttpCookie cookieAnno2HttpCookie(CookiePair annotation, String value) {
        HttpCookie httpCookie = new HttpCookie(annotation.value(),value);
        httpCookie.setComment(annotation.comment());
        httpCookie.setCommentURL(annotation.commentURL());
        httpCookie.setDiscard(annotation.toDiscard());
        httpCookie.setDomain(annotation.domain());
        httpCookie.setHttpOnly(annotation.httpOnly());
        httpCookie.setMaxAge(annotation.maxAge());
        httpCookie.setPath(annotation.path());
        httpCookie.setPortlist(annotation.portlist());
        httpCookie.setValue(value);
        httpCookie.setSecure(annotation.secure());
        httpCookie.setVersion(annotation.version());
        return httpCookie;
    }*/



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
        for (Param methodArg : argList) {
            if (methodArg.getValue() == null){
                continue;
            }

            Object argValue = methodArg.getValue();
            BodyJsonParam annotation = methodArg.getAnnotation(BodyJsonParam.class);
            if (annotation != null){
                return new HttpBodyJSON(getArgFillValue(argValue).toString());
            }

            BodyBinaryParam binaryParam = methodArg.getAnnotation(BodyBinaryParam.class);
            if (binaryParam != null){
                return getHttpBodyBinaryForValue(argValue);
            }

            BodyFormData stringFormParam = methodArg.getAnnotation(BodyFormData.class);
            if (stringFormParam != null){
                if (isObj(argValue.getClass())){
                    return new HttpBodyFormData(objToMap(argValue));
                }
            }
            BodyMultiPartData multipartParam = methodArg.getAnnotation(BodyMultiPartData.class);
            if (multipartParam != null) {
                return getHttpBodyMultipartFormData(argValue, methodArg.getType());
            }
        }
        return null;
    }


    private HttpBodyMultipartFormData getHttpBodyMultipartFormData(Object argValue, Class<?> argClass) {
        List<MultipartFormDataItem> dataItems = new ArrayList<>();
        if (!isObj(argClass)){
            return null;
        }

        for (Param param : new ClassFieldArgList(argValue)) {
            Object fieldValue = param.getValue();
            com.alibaba.fastjson.annotation.JSONField jsonField = param.getAnnotation(com.alibaba.fastjson.annotation.JSONField.class);
            com.alibaba.fastjson2.annotation.JSONField jsonField2 = param.getAnnotation(com.alibaba.fastjson2.annotation.JSONField.class);
            String fieldName = param.getName();
            if (jsonField != null){
                fieldName = jsonField.name();
            }
            if (jsonField2 != null){
                fieldName = jsonField2.name();
            }

            boolean isFile = isFileField(param);
            if (!isFile && isObj(param.getType())){
                // 非File的其他对象不处理
                continue;
            }

            if (!isFile){
                String fieldValueStr = (fieldValue == null ? null : fieldValue.toString());
                dataItems.add(new MultipartFormDataItem(fieldName,fieldValueStr,null,false));
                continue;
            }

            // 文件
            if (!param.getType().isArray() && !Collection.class.isAssignableFrom(param.getType())){
                File onefile = fieldValue == null ? null : (File)fieldValue;
                dataItems.add(new MultipartFormDataItem(fieldName,null,onefile,true));
                continue;
            }

            if (fieldValue == null){
                dataItems.add(new MultipartFormDataItem(fieldName,null,null,true));
                continue;
            }

            // 多文件拆成单个
            File[] fileArr = null;
            if (Collection.class.isAssignableFrom(param.getType())){
                fileArr = ((Collection<File>)fieldValue).toArray(new File[0]);
            }else {
                fileArr = (File[])fieldValue;
            }

            for (File file : fileArr) {
                dataItems.add(new MultipartFormDataItem(fieldName,null,file,true));
            }
        }
        return new HttpBodyMultipartFormData(dataItems);
    }

    private  boolean isFileField(Param param){
        Class<?> clz = param.getType();
        if (File.class.isAssignableFrom(clz)){
            return true;
        }
        return param.isCollection(File.class);
    }


    private Map<String, String> objToMap(Object argValue) {
        return JSON.parseObject(JSON.toJSONString(argValue), new TypeReference<Map<String, String>>() {});
    }


    private HttpBodyBinary getHttpBodyBinaryForValue(Object argValue)  {
        InputStream inputStream = getInputStream(argValue);
        return new HttpBodyBinary(inputStream);
    }


    private InputStream getInputStream(Object argValue) {
        InputStream inputStream = null;
        try {
            if (argValue instanceof InputStream){
                inputStream = (InputStream) argValue;
            }else if (argValue instanceof File){
                inputStream = Files.newInputStream(((File) argValue).toPath());
            } else if (argValue instanceof InputStreamSource){
                inputStream = ((InputStreamSource) argValue).getInputStream();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }

    public Map<String, String> findHeaders(ArgList argList) {
        String[] headers = httpInterface.headers();
        Map<String, String> fixHeaders = Arrays.stream(headers).filter(e -> e.contains("=")).collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1]));
        for (Param methodArg : argList) {
            HeaderParam annotation = methodArg.getAnnotation(HeaderParam.class);
            if (annotation == null){
                continue;
            }

            Object argValue = methodArg.getValue();
            String tmpFiledName = annotation.value();
            boolean isObjFlag = isObj(methodArg.getType());
            if (StringUtils.isBlank(tmpFiledName) && !isObjFlag){
                throw new IllegalArgumentException("use @HeaderParam please specify parameter name");
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
        return fixHeaders;
    }

    public Map<String,String> getHeaderParamForObj(Object obj){
        Map<String,String> map = new HashMap<>();
        for (Param param : new ClassFieldArgList(obj)) {
            Object fieldValue = getArgFillValue(param.getValue());
            String fileName = param.getName();

            if (fieldValue != null && isObj(param.getType())){
                fieldValue = null;
            }
            HeaderParam annotation = param.getAnnotation(HeaderParam.class);
            if (annotation != null){
                fileName = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : fileName;
            }
            map.put(fileName,fieldValue == null ? null : fieldValue.toString());
        }
        return map;
    }

    public Map<String,Object> findQueryParam(ArgList argList) {
        String[] headers = httpInterface.params();
        Map<String, String> queryParam = Arrays.stream(headers).filter(e-> e.contains("=")).collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1]));
        Map<String,Object> queryMap = new HashMap<>(queryParam);
        for (Param param : argList) {
            UrlParam annotation = param.getAnnotation(UrlParam.class);
            if (annotation == null){
                continue;
            }

            String tmpFiledName = annotation.value();
            boolean isObjFlag = isObj(param.getType());
            if (!isObjFlag && StringUtils.isBlank(tmpFiledName)){
                throw new IllegalArgumentException("use @UrlParam please specify parameter name");
            }

            Object argValue = param.getValue();
            Object value = getActualArgValue(argValue);
            if (value == null){
                if (!isObjFlag){
                    queryMap.put(tmpFiledName,null);
                }
                // 为对象直接忽略
                continue;
            }

            if (!isObjFlag){
                queryMap.put(tmpFiledName, getArgFillValue(argValue));
            }else {
                queryMap.putAll(getQueryParamForObj(value));
            }
        }

        return queryMap;
    }

    public Map<String,String> findPathParam(ArgList argList) {
        Map<String,String> queryMap = new HashMap<>();
        for (Param methodArg : argList) {
            PathParam annotation = methodArg.getAnnotation(PathParam.class);
            if (annotation == null){
                continue;
            }

            if (isObj(methodArg.getType())){
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


    public Map<String,Object> getQueryParamForObj(Object obj){
        Map<String,Object> map = new HashMap<>();
        for (Param param : new ClassFieldArgList(obj)) {
            Object fieldValue = getArgFillValue(param.getValue());
            String fileName = param.getName();

            if (fieldValue != null && isObj(param.getType())){
                fieldValue = null;
            }

            UrlParam annotation = param.getAnnotation(UrlParam.class);
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

            Object argValue = methodArg.getValue();
            Class<?> argValueClass = argValue.getClass();
            if (argValueClass.isArray() || List.class.isAssignableFrom(argValueClass)){
                continue;
            }

            CombineParam annotation = methodArg.getAnnotation(CombineParam.class);
            if (annotation == null || !isObj(argValueClass)){
                continue;
            }
            fillHttpMetadata(httpMetadata, new ClassFieldArgList(argValue));
        }
    }

    private void fillHttpMetadata(HttpMetadata httpMetadata, ArgList paramArgList) {
        httpMetadata.putAllQueryParam(findQueryParam(paramArgList));
        httpMetadata.putAllPathParam(findPathParam(paramArgList));
        httpMetadata.setBodyIfAbsent(findHttpBody(paramArgList));
        httpMetadata.putAllHeaders(findHeaders(paramArgList));
        httpMetadata.addAllCookies(findCookies(paramArgList));
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


    public  boolean isObj(Class<?> valueClass){
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
        return false;
    }

    public  boolean isObjOrArr(Class<?> valueClass){
        if (isObj(valueClass)){
            return true;
        }

        if (valueClass.isArray() || List.class.isAssignableFrom(valueClass) || Collection.class.isAssignableFrom(valueClass)){
            return true;
        }
        return false;
    }


    public Object getArgFillValue(Object argValue) {
        Object value = getActualArgValue(argValue);
        if (value == null){
            return null;
        }
        if (value.getClass().getClassLoader() == this.getClass().getClassLoader()
                || value.getClass().isArray()
                || List.class.isAssignableFrom(value.getClass())){
           return JSON.toJSONString(value);
        }
        return value;
    }
}
