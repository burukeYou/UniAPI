package com.burukeyou.uniapi.core.cookie;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author  caizhihao
 */
public class CookieManager implements CookieJar {

    // HttpApiClient - 域名 - cookies列表
    private final Map<String,List<HttpCookie>> cookies = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        String host = httpUrl.host();

    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return null;
    }


}
