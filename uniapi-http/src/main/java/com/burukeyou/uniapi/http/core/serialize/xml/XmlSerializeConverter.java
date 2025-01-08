package com.burukeyou.uniapi.http.core.serialize.xml;

import java.lang.reflect.Type;

import com.burukeyou.uniapi.http.core.serialize.SerializeConverter;

/**
 *  xml
 *
 *  @author caizhihao
 */
public interface XmlSerializeConverter extends SerializeConverter {

    /**
     * Convert the object to an XML string
     * @param object        wait to be converted
     * @return              XML string
     */
    String serialize(Object object);

    /**
     * Convert the XML string to an object
     * @param xml       wait to be converted
     * @param type      the type of the object
     * @return          the object
     */
    Object deserialize(String xml, Type type);

}
