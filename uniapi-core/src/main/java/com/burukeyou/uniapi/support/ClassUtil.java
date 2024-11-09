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


    public static Class<?> getSuperClassParamFirstClass(Class<?> clz){
        Type superclass = clz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType){
              Type[] arr = ((ParameterizedType)superclass).getActualTypeArguments();
              return (Class<?>)arr[0];
        }
        throw new IllegalArgumentException("can not find super class argument");
    }

    public static ParameterizedType getSuperInterfacesParameterizedType(Class<?> clazz,Class<?> genericInterfaceClass) {
        Class<?> current = clazz;
        ParameterizedType genericClassParameterizedType = null;
        while (current != null) {
            Type[] genericInterfaces = current.getGenericInterfaces();
            if (genericInterfaces.length <= 0){
                current = current.getSuperclass();
                continue;
            }

            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType){
                    ParameterizedType parameterizedType =  ((ParameterizedType)genericInterface);
                    if (genericInterfaceClass.equals(parameterizedType.getRawType())){
                        genericClassParameterizedType = parameterizedType;
                    }
                }
            }

            if (genericClassParameterizedType != null){
                break;
            }
            current = current.getSuperclass();
        }
        return genericClassParameterizedType;
    }



}
