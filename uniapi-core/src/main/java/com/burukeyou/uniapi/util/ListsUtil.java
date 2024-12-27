package com.burukeyou.uniapi.util;

import java.util.Collection;
import java.util.Map;

import com.burukeyou.uniapi.support.arg.ArgList;
import com.burukeyou.uniapi.support.arg.EmptyArgList;
import org.springframework.lang.Nullable;


public class ListsUtil {

    private static final ArgList emptyList = new EmptyArgList();

    public static  ArgList emptyArgList(){
        return emptyList;
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }


    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}
