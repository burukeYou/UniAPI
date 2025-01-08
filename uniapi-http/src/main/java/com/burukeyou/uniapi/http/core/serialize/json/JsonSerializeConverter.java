package com.burukeyou.uniapi.http.core.serialize.json;

import java.lang.reflect.Type;

import com.burukeyou.uniapi.http.core.serialize.SerializeConverter;

/**
 *  Json serialize converter
 *
 *  @author caizhihao
 */
public interface JsonSerializeConverter extends SerializeConverter {

    /**
     * Convert the object to a json string
     * @param object        wait to be converted
     * @return              json string
     */
    String serialize(Object object);

    /**
     * Convert the json string to  object
     * @param json       wait to be converted
     * @param type      the type of the object
     * @return          the object
     */
    Object deserialize(String json, Type type);

}
