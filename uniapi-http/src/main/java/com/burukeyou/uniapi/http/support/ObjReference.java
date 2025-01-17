package com.burukeyou.uniapi.http.support;

public class ObjReference<T> {

    private T obj;

    public ObjReference() {

    }

    public ObjReference(T obj) {
        this.obj = obj;
    }

    public static <T> ObjReference<T> of(T obj) {
        return new ObjReference<>(obj);
    }

    public static <T> ObjReference<T> of() {
        return new ObjReference<>();
    }

    public T get() {
        return obj;
    }

    public ObjReference<T> set(T obj) {
        this.obj = obj;
        return this;
    }
}
