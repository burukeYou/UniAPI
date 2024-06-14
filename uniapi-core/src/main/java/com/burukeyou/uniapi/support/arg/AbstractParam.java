package com.burukeyou.uniapi.support.arg;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author caizhihao
 */
public abstract class AbstractParam implements Param {

    @Override
    public boolean isObject() {
        Class<?> type = getType();
        ClassLoader classLoader = type.getClassLoader();
        if (type.isPrimitive() || type.isEnum()){
            return false;
        }
        if (type.isArray() || Collection.class.isAssignableFrom(type)){
            return false;
        }
        if (classLoader == this.getClass().getClassLoader()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isCollection() {
        Class<?> type = getType();
        if (type.isArray()  || Collection.class.isAssignableFrom(type)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isCollection(Class<?> elementClass) {
        Class<?> clz = getType();
        if (clz.isArray() && elementClass.isAssignableFrom(clz.getComponentType())){
            return true;
        }

        if (Collection.class.isAssignableFrom(clz)) {
            Type genericType = getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericType;
                Type[] typeArguments = paramType.getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                    Class<?> elementType = (Class<?>) typeArguments[0];
                    if (elementClass.isAssignableFrom(elementType)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public <T> List<T> castListValue(Class<T> elementClass) {
        if (!isCollection(elementClass)){
            throw new ClassCastException("cant not cast to List<" + elementClass.getSimpleName() + "> type for " + getGenericType());
        }

        Object value = getValue();
        if (value == null){
            return null;
        }

        List<T> result;
        Class<?> clz = getType();
        if (clz.isArray()) {
            T[] dataArr = (T[]) value;
            result = Arrays.asList(dataArr);
        }else {
            result = new ArrayList<>((Collection<T>)value);
        }

        return result;
    }
}
