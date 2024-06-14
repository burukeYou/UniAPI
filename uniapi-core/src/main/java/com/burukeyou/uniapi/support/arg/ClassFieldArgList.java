package com.burukeyou.uniapi.support.arg;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  caizhihao
 */
public class ClassFieldArgList extends ArgList {

    private final List<Param> paramsList = new ArrayList<>();

    public ClassFieldArgList(Object targetObj) {
        for (FiledParam param : new ClassFieldIterable(targetObj)) {
            paramsList.add(param);
        }
    }

    @Override
    public Param get(int index) {
        return paramsList.get(index);
    }

    @Override
    public Iterator<Param> iterator() {
        return paramsList.iterator();
    }

    @Override
    public int size() {
        return paramsList.size();
    }

    private static class ClassFieldIterable implements Iterable<FiledParam>{

        private final Object targetObject;

        public ClassFieldIterable(Object targetObject) {
            this.targetObject = targetObject;
        }


        @Override
        public Iterator<FiledParam> iterator() {
            return new ClassFieldArgsListIterator(targetObject);
        }
    }

    private static class ClassFieldArgsListIterator implements Iterator<FiledParam> {

        private final List<Field> fieldList = new ArrayList<>();

        private int index = 0;

        private final Object targetObj;

        private  ClassFieldArgsListIterator(Object targetObj){
            this.targetObj = targetObj;
            ReflectionUtils.doWithFields(targetObj.getClass(), field -> {
                if(Modifier.isStatic(field.getModifiers())){
                    return;
                }
                fieldList.add(field);
            });
        }

        @Override
        public boolean hasNext() {
            return index < fieldList.size();
        }

        @Override
        public FiledParam next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more elements");
            }
            Field field = fieldList.get(index);
            index++;
            return new FiledParam(targetObj,field);
        }
    }
}
