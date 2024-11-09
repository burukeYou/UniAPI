package com.burukeyou.uniapi.http.core.conveter.response;


import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.core.response.HttpFileResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.UniHttpApiConstant;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.TimeUtil;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class HttpFileResponseConverter extends AbstractHttpResponseBodyConverter {

    @Override
    protected boolean isConvert(Response response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        Class<?> returnType = methodInvocation.getMethod().getReturnType();
        if(File.class.isAssignableFrom(returnType) || HttpFileResponse.class.equals(returnType)){
            return true;
        }
        return HttpResponse.class.equals(returnType) && isGenericType(File.class,methodInvocation);
    }

    @Override
    protected HttpFileResponse doConvert(Response response, MethodInvocation methodInvocation) {
        return doWithHttpFileResponse(response,methodInvocation);
    }

    private HttpFileResponse doWithHttpFileResponse(Response response, MethodInvocation methodInvocation) {
        String savePath = getSavePath(response,methodInvocation);
        ResponseBody responseBody = response.body();
        InputStream inputStream = responseBody.byteStream();
        createDirIfNotExist(Paths.get(savePath).getParent().toString());
        try {
            File file = new File(savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileCopyUtils.copy(inputStream,fileOutputStream);
            return new HttpFileResponse(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSavePath(Response response, MethodInvocation methodInvocation) {
        String savePath = null;
        // 方法参数上获取
        for (Param methodArg : new MethodArgList(methodInvocation.getMethod(),methodInvocation.getArguments())) {
            ResponseFile annotation = methodArg.getAnnotation(ResponseFile.class);
            if (annotation == null){
                continue;
            }

            if (!String.class.equals(methodArg.getType())){
                throw new IllegalArgumentException("@ResponseFile use in method param must be marked on String type ");
            }
            savePath = (String)methodArg.getValue();
        }

        String fileName = getFileResponseName(response);
        if (StringUtils.isBlank(fileName)){
            fileName = TimeUtil.formatPure(LocalDateTime.now());
        }

        if (savePath != null){
            if (isFileForPath(savePath)){
                return savePath;
            }
            return joinPath(savePath,fileName);
        }

        //
        String saveDir = UniHttpApiConstant.DEFAULT_FILE_SAVE_DIR;
        saveDir = getEnvironmentValue(saveDir);
        ResponseFile responseFileAnno = AnnotatedElementUtils.getMergedAnnotation(methodInvocation.getMethod(),ResponseFile.class);
        if (responseFileAnno != null){
            saveDir = responseFileAnno.saveDir();
        }
        if (responseFileAnno == null || responseFileAnno.uuid()){
            saveDir = joinPath(saveDir, getUUID());
        }
        if (saveDir.contains("{YYYYMMDD}")){
            saveDir = saveDir.replace("{YYYYMMDD}", TimeUtil.formatPure(LocalDate.now()));
        }

        savePath = joinPath(saveDir,fileName);
        return savePath;
    }


}
