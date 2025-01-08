package com.burukeyou.uniapi.http.core.serialize.json;

import java.lang.reflect.Type;

import org.springframework.stereotype.Component;


/**
 * FastJson2序列化转换器
 *
 * @see com.alibaba.fastjson2.JSON
 * @author caizhihao
 */
@Component
public class FastJson2SerializeConverter implements JsonSerializeConverter {

    @Override
    public String serialize(Object object) {
        return com.alibaba.fastjson2.JSON.toJSONString(object);
    }

    @Override
    public Object deserialize(String json, Type type) {
        return com.alibaba.fastjson2.JSON.parseObject(json, type);
    }

}
