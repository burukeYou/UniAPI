package com.burukeyou.uniapi.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtil {

    private ClassUtil(){}

    public static Type[] getSuperClassActualTypeArguments(Class<?> clz){
        Type superclass = clz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType){
            return  ((ParameterizedType)superclass).getActualTypeArguments();
        }
        throw new IllegalArgumentException("can not find super class argument");
    }

    public static Type[] getSuperInterfaceActualTypeArguments(Class<?> clz){
        Type[] genericInterfaces = clz.getGenericInterfaces();
        if (genericInterfaces[0] instanceof ParameterizedType){
            return  ((ParameterizedType)genericInterfaces[0]).getActualTypeArguments();
        }
        throw new IllegalArgumentException("can not find super interface argument");
    }
}
