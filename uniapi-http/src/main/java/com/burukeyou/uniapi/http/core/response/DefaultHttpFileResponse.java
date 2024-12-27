package com.burukeyou.uniapi.http.core.response;

import java.io.File;
import java.io.InputStream;

import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.util.FileBizUtil;

/**
 * @author caizhihao
 */
public class DefaultHttpFileResponse extends DefaultHttpResponse implements HttpFileResponse<Object> {

    public DefaultHttpFileResponse(UniHttpResponse uniHttpResponse, String originBodyPrintString, Object bodyResult) {
        super(uniHttpResponse, originBodyPrintString, bodyResult);
    }

    @Override
    public String getFileName() {
        return getContentDispositionFileName();
    }

    @Override
    public File saveFile(String savePath) {
        if (bodyResult == null) {
           throw new IllegalStateException("response body is null can not save file to local");
        }
        if (bodyResult instanceof File){
            return FileBizUtil.moveFile((File) bodyResult, savePath);
        }

        if(InputStream.class.isAssignableFrom(bodyResult.getClass())){
            return FileBizUtil.saveFile((InputStream) bodyResult, savePath);
        }

        if (bodyResult instanceof byte[]){
            return FileBizUtil.saveFile((byte[]) bodyResult, savePath);
        }

        throw new IllegalStateException("can not save file for body result type " + bodyResult.getClass().getName());
    }


}
