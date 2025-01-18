package com.burukeyou.uniapi.http.support.function;

@FunctionalInterface
public interface B3Function<T1, T2, T3,R> {

    R apply(T1 t1, T2 t2,T3 t3);

}
