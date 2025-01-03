package com.burukeyou.uniapi.http.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;


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



}
