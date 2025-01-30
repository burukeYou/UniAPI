package com.burukeyou.uniapi.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author  caizhihao
 */
public class ClzUtil {

    private ClzUtil(){}

    /**
     * get Type actually class type
     */
    public static Class<?> resolveClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }

        if (type instanceof TypeVariable) {
            return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
        }

        if (type instanceof WildcardType) {
            return Object.class;
        }

        throw new IllegalArgumentException("Unsupported type resolve : " + type);
    }
}
