package com.burukeyou.uniapi.http.support;

import java.net.Proxy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpProxyConfig {

    private  Proxy.Type type;

    private  String address;

    private  String username;

    private  String password;

}
