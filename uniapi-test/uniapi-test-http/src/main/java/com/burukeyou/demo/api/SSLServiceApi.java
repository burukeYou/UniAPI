package com.burukeyou.demo.api;

import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.annotation.HttpApi;
import com.burukeyou.uniapi.http.annotation.SslCfg;
import com.burukeyou.uniapi.http.annotation.param.QueryPar;
import com.burukeyou.uniapi.http.annotation.request.GetHttpInterface;

@HttpApi(
        //url = "https://www.bkt01.com:8999"
        url = "https://10.94.22.74:8999"
        //httpClient = ChannelSingleSSLOkhttpClientFactory.class
       // httpClient = ChannelPairSSLOkhttpClientFactory.class
)
@SslCfg(
        //trustCertificate = "${channel.mtuan.ssl.cert2}",
        trustStore = "${channel.mtuan.ssl.keyStore1}",
        //trustStore = "${channel.mtuan.ssl.keyStore2}",
        trustStoreType = "PKCS12",
        trustStorePassword = "changeit"
)
public interface SSLServiceApi {

    @GetHttpInterface("/ssl-web/get01")
    BaseRsp<String> get01(@QueryPar("name") String name);

}
