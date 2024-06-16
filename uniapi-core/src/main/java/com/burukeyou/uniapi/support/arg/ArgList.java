package com.burukeyou.uniapi.support.arg;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author caizhihao
 */
public abstract class ArgList extends AbstractList<Param> implements Iterable<Param> {

    protected final List<Param> paramsList = new ArrayList<>();

    @Override
    public Iterator<Param> iterator() {
        return paramsList.iterator();
    }


    @Override
    public Param get(int index) {
        return paramsList.get(index);
    }


    @Override
    public int size() {
        return paramsList.size();
    }

}