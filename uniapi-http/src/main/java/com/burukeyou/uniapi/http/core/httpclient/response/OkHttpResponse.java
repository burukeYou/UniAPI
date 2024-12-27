package com.burukeyou.uniapi.http.core.httpclient.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

/**
 * OkHttpResponse
 * @author      caizhihao
 */

@Getter
@Setter
public class OkHttpResponse implements HttpResponseInfo {

    private Response response;

    public OkHttpResponse(Response response) {
        this.response = response;
    }


    @Override
    public String getBodyToString() {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }

    @Override
    public byte[] getBodyBytes() {
        if (response.body() == null){
            return null;
        }
        try {
            return response.body().bytes();
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }

    @Override
    public InputStream getBodyToInputStream() {
        if (response.body() == null){
            return null;
        }
        return response.body().byteStream();
    }


    @Override
    public Map<String, List<String>> getHeaderMap() {
        return response.headers().toMultimap();
    }


    @Override
    public int getHttpCode() {
        return response.code();
    }

    @Override
    public void closeResource() {
        if (response.body() != null){
            response.close();
        }
    }

}
