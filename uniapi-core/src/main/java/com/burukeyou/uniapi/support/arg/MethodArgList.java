package com.burukeyou.uniapi.support.arg;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  caizhihao
 */
public class MethodArgList extends ArgList {

    public MethodArgList(Method method, Object[] args) {
        for (MethodParam param : new MethodArgIterable(method,args)) {
            paramsList.add(param);
        }
    }

    private static class MethodArgIterable implements Iterable<MethodParam>{

        private final Parameter[] parameters;
        private final Object[] args;

        public MethodArgIterable(Method method, Object[] args) {
            this.parameters = method.getParameters();
            this.args = args;
        }



        @Override
        public Iterator<MethodParam> iterator() {
            return new MethodArgsListIterator(parameters,args);
        }
    }

    private static class MethodArgsListIterator implements Iterator<MethodParam> {

        private final Parameter[] parameters;
        private final Object[] args;

        private int index = 0;

        public MethodArgsListIterator(Parameter[] parameters, Object[] args) {
            this.parameters = parameters;
            this.args = args;
        }

        @Override
        public boolean hasNext() {
            return index < args.length;
        }

        @Override
        public MethodParam next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more elements");
            }
            MethodParam methodParam = new MethodParam(parameters[index], args[index]);
            index++;
            return methodParam;
        }
    }
}
