package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.config.SpringBeanContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  caizhihao
 */
public class HttpResponseBodyConverterChain {

    private static volatile HttpResponseConverter chain;

    private static final List<Class<? extends HttpResponseConverter>> orderClassList = Arrays.asList(
            HttpJsonResponseConverter.class,
            HttpTextResponseConverter.class,
            HttpBinaryResponseConverter.class,
            HttpFileDownloadLocalResponseConverter.class,
            HttpInputStreamResponseConverter.class,
            HttpEmptyResponseConverter.class
    );

    public HttpResponseBodyConverterChain() {
        if (chain == null){
            synchronized (HttpResponseBodyConverterChain.class){
                if (chain == null){
                    List<HttpResponseConverter> converters = SpringBeanContext.listBean(HttpResponseConverter.class);
                    sorterConverters(converters);
                    for (int i = 0; i < converters.size() - 1; i++) {
                        converters.get(i).setNext(converters.get(i+1));
                    }
                    chain = converters.get(0);
                }
            }
        }


    }

    private void sorterConverters(List<HttpResponseConverter> converters) {
        Map<Class<? extends HttpResponseConverter>,Integer> orderMap = new HashMap<>();
        for (int i = 0; i < orderClassList.size(); i++) {
            orderMap.put(orderClassList.get(i),i);
        }
        converters.sort((a, b) -> {
            Integer aLevel = orderMap.get(a.getClass());
            Integer bLevel = orderMap.get(b.getClass());
            if (aLevel == null && bLevel == null) {
                return 0;
            }
            if (aLevel == null){
                return 1;
            }
            if (bLevel == null){
                return 1;
            }
            return aLevel.compareTo(bLevel);
        });
    }

    public HttpResponseConverter getChain() {
        return chain;
    }
}
