package com.burukeyou.uniapi.http.core.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.burukeyou.uniapi.exception.BaseUniApiException;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.request.HttpInterface;
import com.burukeyou.uniapi.http.annotation.param.*;
import com.burukeyou.uniapi.http.core.request.*;
import com.burukeyou.uniapi.http.support.Cookie;
import com.burukeyou.uniapi.support.arg.*;
import com.burukeyou.uniapi.util.ListsUtil;
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
        return httpMetadata;
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

    private List<Cookie> findCookies(ArgList argList) {
        List<Cookie> cookies = new ArrayList<>(parseCookie(getEnvironmentValue(httpInterface.cookie())));
        for (Param param : argList) {
            Object argValue = param.getValue();
            if (argValue == null){
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

            if (StringUtils.isNotBlank(annotation.value())){
                // 指定了name 当成单个cookie处理
                if (String.class.equals(param.getType())){
                    cookies.add(new Cookie(annotation.value(),argValue.toString()));
                    continue;
                }
            }

            // 为指定name当成cookies string 处理
            if (String.class.equals(param.getType())){
                cookies.addAll(parseCookie(argValue.toString()));
            }else if (param.isCollection(String.class)){
                for (String cookieStr : param.castListValue(String.class)) {
                    cookies.addAll(parseCookie(cookieStr));
                }
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
        for (Param methodArg : argList) {
            if (methodArg.getValue() == null){
                continue;
            }

            Object argValue = methodArg.getValue();
            BodyJsonPar annotation = methodArg.getAnnotation(BodyJsonPar.class);
            if (annotation != null){
                return new HttpBodyJSON(getArgFillValue(argValue).toString());
            }

            BodyBinaryPar binaryParam = methodArg.getAnnotation(BodyBinaryPar.class);
            if (binaryParam != null){
                return getHttpBodyBinaryForValue(argValue);
            }

            BodyFormPar stringFormParam = methodArg.getAnnotation(BodyFormPar.class);
            if (stringFormParam != null){
                if (isObjOrMap(argValue.getClass())){
                    return new HttpBodyFormData(objToMap(argValue));
                }else if (!methodArg.isCollection()){
                    if (StringUtils.isBlank(stringFormParam.value())){
                        throw new BaseUniApiException("user @BodyFormPar for single value please specify the parameter name ");
                    }

                    // 单个
                    return new HttpBodyFormData(Collections.singletonMap(stringFormParam.value(),getArgFillValue(argValue).toString()));
                }
            }

            BodyMultiPartPar multipartParam = methodArg.getAnnotation(BodyMultiPartPar.class);
            if (multipartParam != null) {
                boolean nameExistFlag = StringUtils.isNotBlank(multipartParam.value());
                if (nameExistFlag && File.class.isAssignableFrom(methodArg.getType())){
                    MultipartFormDataItem dataItem = new MultipartFormDataItem(multipartParam.value(),null,(File)argValue,true);
                    return new HttpBodyMultipartFormData(Collections.singletonList(dataItem));
                } else if (isObjOrMap(methodArg.getType())){
                    return getHttpBodyMultipartFormData(argValue, methodArg.getType());
                }else if (nameExistFlag){
                    // 单个
                    MultipartFormDataItem dataItem = new MultipartFormDataItem(multipartParam.value(),argValue.toString(),null,false);
                    return new HttpBodyMultipartFormData(Collections.singletonList(dataItem));
                }
            }
        }
        return null;
    }


    private HttpBodyMultipartFormData getHttpBodyMultipartFormData(Object argValue, Class<?> argClass) {
        List<MultipartFormDataItem> dataItems = new ArrayList<>();

        ArgList argList = autoGetArgList(argValue);
        for (Param param : argList) {
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
            if (!isFile && isObjOrMap(param.getType())){
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
        Map<String, String> fixHeaders = Arrays.stream(headers)
                .filter(e -> e.contains("=") || e.contains(":"))
                .collect(Collectors.toMap(e -> e.split("[=:]")[0].trim(), e -> e.split("[=:]")[1].trim()));

        for (Param methodArg : argList) {
            HeaderPar annotation = methodArg.getAnnotation(HeaderPar.class);
            if (annotation == null || methodArg.isCollection()){
                continue;
            }

            Object argValue = methodArg.getValue();
            String tmpFiledName = annotation.value();
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
            for (String item  :paramStr.split("&")){
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

            String tmpFiledName = annotation.value();
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

    /**
     * 判断是否是普通值
     *      除了自定义对象、Map、集合之外的所有对象都是基本类型，
     *      表示的是单一值，比如int、Boolean、String
     */
    public boolean isBaseType(Class<?> valueClass){
        if (valueClass.isPrimitive()){
            return true;
        }
        return !isObjOrMap(valueClass) && !isObjOrArr(valueClass);
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
