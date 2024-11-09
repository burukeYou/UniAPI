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

    public ClassFieldArgList(Object targetObj) {
        for (FieldParam param : new ClassFieldIterable(targetObj)) {
            paramsList.add(param);
        }
    }

    private static class ClassFieldIterable implements Iterable<FieldParam>{

        private final Object targetObject;

        public ClassFieldIterable(Object targetObject) {
            this.targetObject = targetObject;
        }


        @Override
        public Iterator<FieldParam> iterator() {
            return new ClassFieldArgsListIterator(targetObject);
        }
    }

    private static class ClassFieldArgsListIterator implements Iterator<FieldParam> {

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
        public FieldParam next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more elements");
            }
            Field field = fieldList.get(index);
            index++;
            return new FieldParam(targetObj,field);
        }
    }
}
