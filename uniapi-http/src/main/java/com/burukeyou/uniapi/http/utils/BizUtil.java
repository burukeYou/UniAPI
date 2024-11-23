package com.burukeyou.uniapi.http.utils;

import java.io.File;
import java.io.InputStream;


public class BizUtil {

    public static boolean isFileForClass(Class<?> clz){
        return File.class.isAssignableFrom(clz) || byte[].class.equals(clz) || InputStream.class.isAssignableFrom(clz);
    }
}
