package com.burukeyou.uniapi.http.core.conveter.response;


import com.burukeyou.uniapi.http.annotation.ResponseFile;
import com.burukeyou.uniapi.http.core.channel.HttpApiMethodInvocation;
import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.response.HttpFileDownloadLocalResponse;
import com.burukeyou.uniapi.http.core.http.response.UniHttpResponse;
import com.burukeyou.uniapi.http.support.UniHttpApiConstant;
import com.burukeyou.uniapi.support.arg.MethodArgList;
import com.burukeyou.uniapi.support.arg.Param;
import com.burukeyou.uniapi.util.TimeUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class HttpFileDownloadLocalResponseConverter extends AbstractHttpResponseConverter {

    @Override
    protected boolean isConvert(UniHttpResponse response, MethodInvocation methodInvocation) {
        if (!isFileDownloadResponse(response)){
            return false;
        }
        return isFileReturnType(File.class,methodInvocation);
    }

    @Override
    protected HttpFileDownloadLocalResponse doConvert(ResponseConvertContext context) {
        return doWithHttpFileResponse(context);
    }

    private HttpFileDownloadLocalResponse doWithHttpFileResponse(ResponseConvertContext context) {
        UniHttpResponse response = context.getResponse();
        HttpApiMethodInvocation<Annotation> methodInvocation = context.getMethodInvocation();
        String savePath = getSavePath(response,methodInvocation);
        InputStream inputStream = response.getBodyToInputStream();
        createDirIfNotExist(Paths.get(savePath).getParent().toString());
        try {
            File file = new File(savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileCopyUtils.copy(inputStream,fileOutputStream);
            return new HttpFileDownloadLocalResponse(file,context);
        } catch (IOException e) {
            throw new UniHttpResponseException(e);
        }
    }

    private String getSavePath(UniHttpResponse response, MethodInvocation methodInvocation) {
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
