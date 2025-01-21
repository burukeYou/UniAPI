package com.burukeyou.uniapi.http.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.burukeyou.uniapi.config.SpringBeanContext;
import org.apache.commons.lang3.StringUtils;


@SuppressWarnings("ALL")
public class BizUtil {

    public static boolean isFileForClass(Class<?> clz){
        return File.class.isAssignableFrom(clz) || byte[].class.equals(clz) || InputStream.class.isAssignableFrom(clz);
    }

    /**
     * Rude judgment on whether it is a file path or not
     */


    public static String base64Decode(String content) {
        if (StringUtils.isBlank(content)){
            return content;
        }
        try {
            content = content.trim();
            return new String(Base64.getDecoder().decode(content.getBytes()));
        } catch (Exception e) {
            return content;
        }
    }

    public static byte[] base64DecodeToByte(String content) {
        if (StringUtils.isBlank(content)){
            return new byte[0];
        }
        try {
            content = content.trim();
            return Base64.getDecoder().decode(content.getBytes());
        } catch (Exception e) {
            return content.getBytes();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *  方法返回值的泛型是否是指定类型
     */
    protected boolean isGenericTypeForMethod(Class<?> clz, Method method) {
        Type genericReturnType = method.getGenericReturnType();
        if(genericReturnType instanceof ParameterizedType){
            Type[] arr = ((ParameterizedType)genericReturnType).getActualTypeArguments();
            Type type = arr[0];
            if (type instanceof  Class){
                Class<?>  parameterTypes = (Class<?>)type;
                if (clz.isAssignableFrom(parameterTypes)){
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<String, String> objToMap(Object argValue) {
        if (argValue == null){
            return new HashMap<>(0);
        }
        return JSON.parseObject(JSON.toJSONString(argValue), new TypeReference<Map<String, String>>() {});
    }

    public static <T> T getBeanOrNew(Class<T> beanClass){
        T bean = SpringBeanContext.getBean(beanClass);
        if (bean != null){
            return bean;
        }
        try {
            return beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

/*    public static String getJsonFieldName(Param param){
        String name = null;
        JSONField jsonField2 = param.getAnnotation(JSONField.class);
        if (jsonField2 != null && StrUtil.isNotBlank(jsonField2.name())){
            name = jsonField2.name();
        }
        com.alibaba.fastjson.annotation.JSONField jsonField1 = param.getAnnotation(com.alibaba.fastjson.annotation.JSONField.class);
        if (jsonField1 != null && StrUtil.isNotBlank(jsonField1.name())){
            name = jsonField1.name();
        }
        JsonProperty annotation = param.getAnnotation(JsonProperty.class);
        if (annotation != null && StrUtil.isNotBlank(annotation.value())){
            name = annotation.value();
        }
        if (StrUtil.isBlank(name)){
            name = param.getName();
        }
        return name;
    }*/

    public static <T> T convertValueType(Object value, Class<T> type) {
        if (value == null){
            return null;
        }

        Class<?> valueClass = value.getClass();
        if (valueClass == type || type.isAssignableFrom(valueClass)){
            return (T) value;
        }

        if (Object.class == type){
            return (T) value;
        }
        if (String.class == type){
            return (T)value.toString();
        }
        if (Long.class == type || long.class == type){
            return (T)Long.valueOf(value.toString());
        }
        if (Integer.class == type || int.class == type){
            return (T)Integer.valueOf(value.toString());
        }
        if (Double.class == type || double.class == type){
            return (T)Double.valueOf(value.toString());
        }
        if (Float.class == type || float.class == type){
            return (T)Float.valueOf(value.toString());
        }
        if (Boolean.class == type || boolean.class == type){
            return (T)Boolean.valueOf(value.toString());
        }
        return JSON.parseObject(value.toString(), type);
    }

    public static String getFieldAbsoluteName(Field field) {
        return field.getDeclaringClass().getName() +"." + field.getName();
    }

    public static String getMethodAbsoluteName(Method method) {
        return method.getDeclaringClass().getName() +"." + method.getName();
    }
}
