package com.burukeyou.uniapi.http.utils.cookie;

import java.util.List;
import java.util.stream.Collectors;

import com.burukeyou.uniapi.http.support.Cookie;

public class CookieUtil {


    public static List<Cookie> parseAll(List<String> setCookiesString) {
        return setCookiesString.stream().flatMap(e -> Cookie.parse(e).stream()).collect(Collectors.toList());
    }

}
