package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.exception.BaseUniHttpException;
import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.response.HttpFileResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author caizhihao
 */
public abstract class AbstractHttpResponseConverter implements HttpResponseConverter {

    @Autowired
    private Environment environment;

    protected HttpResponseConverter next;

    private static final Pattern pattern = Pattern.compile("filename\\s*=\\s*\\\"(.*)\\\"");

    @Override
    public HttpResponse<?> convert(ResponseConvertContext context) {
        if (isConvert(context.getResponse(),context.getMethodInvocation())){
            return doConvert(context);
        }
        if (next == null){
            return null;
        }
        return next.convert(context);
    }

    @Override
    public void setNext(HttpResponseConverter nextConverter) {
        this.next = nextConverter;
    }

    protected abstract boolean isConvert(Response response, MethodInvocation methodInvocation);

    protected  abstract HttpResponse<?> doConvert(ResponseConvertContext context);


    protected String getResponseContentType(Response response){
        return response.header("Content-Type");
    }

    protected static void createDirIfNotExist(String baseDir) {
        Path dir = Paths.get(baseDir);
        boolean notExists = Files.notExists(dir);
        if (notExists) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new UniHttpResponseException("创建目录失败" + baseDir,e);
            }
        }
    }

    protected String getFileResponseName(Response response){
        String header = response.header("Content-Disposition");
        if(StringUtils.isBlank(header)){
            return null;
        }
        String fileName = null;
        Matcher matcher = pattern.matcher(header);
        if (matcher.find()){
            fileName = matcher.group(1);
        }
        return fileName;
    }

    protected   String joinPath(Object...paths){
        List<String> cleanPaths = new ArrayList<>();
        for (Object tmp : paths) {
            String path = tmp.toString();
            if (path.startsWith(File.separator)){
                path = path.substring(1);
            }
            if (path.endsWith(File.separator)){
                path = path.substring(0, path.length() - 1);
            }
            cleanPaths.add(path);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(File.separator);
        for (String cleanPath : cleanPaths) {
            stringBuilder.append(cleanPath).append(File.separator);
        }
        return stringBuilder.toString();
    }
    protected boolean isFileForPath(String path) {
        return Paths.get(path).getFileName().toString().contains(".");
    }

    protected String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected  <T> T getEnvironmentValue(T value){
        if (value == null){
            return null;
        }
        if(value.getClass() != String.class){
            return value;
        }
        return (T)environment.resolvePlaceholders(value.toString());
    }

    protected boolean isFileDownloadResponse(Response response){
        String contentType = getResponseContentType(response);
        if (MediaTypeEnum.isFileType(contentType)){
            return true;
        }

        // Content-Disposition: attachment; filename=xxx.txt
        String disposition = response.header("Content-Disposition");
        if(StringUtils.isNotBlank(disposition) && disposition.contains("attachment")){
            return true;
        }
        return false;
    }

    protected boolean isGenericType(Class<?> clz,MethodInvocation methodInvocation) {
        Type genericReturnType = methodInvocation.getMethod().getGenericReturnType();
        if(genericReturnType instanceof ParameterizedType){
            Type[] arr = ((ParameterizedType)genericReturnType).getActualTypeArguments();
            Type type = arr[0];
            if (type instanceof  Class){
                Class<?>  parameterTypes = (Class<?>)type;
                if (clz.isAssignableFrom(parameterTypes)){
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isFileReturnType(Class<?> paramType,MethodInvocation methodInvocation){
        Class<?> returnType = methodInvocation.getMethod().getReturnType();
        if(paramType.equals(returnType)){
            return true;
        }
        return (HttpResponse.class.equals(returnType) || HttpFileResponse.class.equals(returnType)) && isGenericType(paramType,methodInvocation);
    }

    // 获取反序列化body后class的类型
    protected Type getBodyResultType(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Type type = null;
        if (HttpResponse.class.isAssignableFrom(method.getReturnType())){
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType){
                    type = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                }
        }else {
            type = method.getGenericReturnType();
        }
        return type;
    }

    protected String postAfterBodyString(String originString,
                                         HttpResponse<?> httpJsonResponse,
                                         ResponseConvertContext context){
       return context.getProcessor().postAfterHttpResponseBodyString(originString,httpJsonResponse,context.getHttpMetadata(),context.getMethodInvocation());
    }

    protected String getResponseBodyString(Response response){
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new BaseUniHttpException(e);
        }
    }
}
