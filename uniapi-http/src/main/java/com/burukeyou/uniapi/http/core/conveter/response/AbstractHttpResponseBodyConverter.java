package com.burukeyou.uniapi.http.core.conveter.response;

import com.burukeyou.uniapi.http.core.exception.UniHttpResponseException;
import com.burukeyou.uniapi.http.core.response.HttpFileResponse;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.MediaTypeEnum;
import okhttp3.Response;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.io.File;
import java.io.IOException;
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
public abstract class AbstractHttpResponseBodyConverter implements HttpResponseConverter {

    @Autowired
    private Environment environment;

    protected HttpResponseConverter next;

    private static final Pattern pattern = Pattern.compile("filename\\s*=\\s*\\\"(.*)\\\"");

    @Override
    public HttpResponse<?> convert(Response response, MethodInvocation methodInvocation) {
        if (isConvert(response,methodInvocation)){
            return doConvert(response,methodInvocation);
        }
        if (next == null){
            return null;
        }
        return next.convert(response,methodInvocation);
    }

    @Override
    public void setNext(HttpResponseConverter nextConverter) {
        this.next = nextConverter;
    }

    protected abstract boolean isConvert(Response response, MethodInvocation methodInvocation);

    protected  abstract HttpResponse<?> doConvert(Response response, MethodInvocation methodInvocation);


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
            if (type instanceof WildcardTypeImpl){
                // not support ? param type
                return false;
            }
            Class<?>  parameterTypes = (Class<?>)arr[0];
            if (clz.isAssignableFrom(parameterTypes)){
                return true;
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

}
