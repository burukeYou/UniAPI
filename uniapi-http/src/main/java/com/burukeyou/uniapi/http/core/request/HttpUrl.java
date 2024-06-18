package com.burukeyou.uniapi.http.core.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author caizhihao
 */
@Data
@SuperBuilder
@AllArgsConstructor
@Builder
public class HttpUrl implements Serializable {

    private static final long serialVersionUID = 2542439550827242518L;

    private static Pattern PATH_REGREX = Pattern.compile("\\{\\w+\\}");

    private String url = "";
    private String path = "";

    private Map<String,Object> queryParam = new HashMap<>();

    private Map<String,String> pathParam = new HashMap<>();

    public HttpUrl() {
        this.queryParam = new HashMap<>();
        this.pathParam = new HashMap<>();
    }

    public String fillPath(){
        // 路径变量填值
        Matcher matcher = PATH_REGREX.matcher(path);
        StringBuffer pathSB = new StringBuffer();

        String path = this.path.trim();
        boolean exist = matcher.find();
        if (!exist){
            return path;
        }

        do {
            String group = matcher.group();
            group = group.replace("{","").replace("}","");
            Object groupData = pathParam.get(group);
            matcher.appendReplacement(pathSB,String.valueOf(groupData));
        } while (matcher.find());

        matcher.appendTail(pathSB);
        path = pathSB.toString();
        return path;
    }

    public String toUrl(){
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(fillPath());
        if (queryParam != null && !queryParam.isEmpty()){
            String urlParam = queryParam.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse("");
            sb.append("?").append(urlParam);
        }
        return sb.toString();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void putUrlParam(String key, Object value) {
        queryParam.put(key,value);
    }

    public void putIfAbsentUrlParam(String key, Object value) {
        queryParam.putIfAbsent(key,value);
    }

    public void putPathParam(String key, Object value) {
        pathParam.put(key,value == null ? "null" :value.toString());
    }

    @Override
    public String toString() {
        return toUrl();
    }

    public Map<String, Object> getQueryParam() {
        if (queryParam == null){
            queryParam = new HashMap<>();
        }
        return queryParam;
    }

    public Map<String, String> getPathParam() {
        if (pathParam == null){
            pathParam = new HashMap<>();
        }

        return pathParam;
    }
}
