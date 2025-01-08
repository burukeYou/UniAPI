package com.burukeyou.uniapi.http.core.conveter.request;

import com.burukeyou.uniapi.http.core.channel.AbstractHttpMetadataParamFinder;

/**
 *
 * @author  caizhihao
 */
public class HttpRequestBodyConverterChain {

    private final HttpRequestBodyConverter chain;

    private HttpRequestBodyConverter[] getChainConverter(AbstractHttpMetadataParamFinder paramFinder) {
        return new HttpRequestBodyConverter[]{
                new BodyJsonParHttpBodyConverter(paramFinder),
                new BodyTextParHttpBodyConverter(paramFinder),
                new BodyFormParHttpBodyConverter(paramFinder),
                new BodyBinaryHttpBodyConverter(paramFinder),
                new BodyMultiPartParHttpBodyConverter(paramFinder),
                new BodyXmlParHttpBodyConverter(paramFinder)
        };
    }

    public HttpRequestBodyConverter getChain() {
        return chain;
    }

    public HttpRequestBodyConverterChain(AbstractHttpMetadataParamFinder paramFinder) {
            HttpRequestBodyConverter[] arr = getChainConverter(paramFinder);
            for (int i = 0; i < arr.length - 1; i++) {
                arr[i].setNext(arr[i+1]);
            }
            chain = arr[0];
    }


}
