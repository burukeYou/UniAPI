package com.burukeyou.uniapi.http.core.serialize.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JAXB XML SerializeConverter implement
 *
 * @author caizhihao
 */
public class JaxbXmlSerializeConverter implements XmlSerializeConverter {

    private final static Map<Class<?>, JAXBContext> jaxbContextMap = new ConcurrentHashMap<>();

    @Override
    public String serialize(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CharSequence) {
            return object.toString();
        }
        try {
            Marshaller marshaller = getMarshaller(object.getClass());
            StringWriter result = new StringWriter();
            marshaller.marshal(object, result);
            return result.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(String xml, Type type) {
        try {
            Object unmarshal = getUnMarshaller(resolveClass(type)).unmarshal(new StringReader(xml));
            return (T) unmarshal;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Marshaller getMarshaller(Class<?> objClass) throws JAXBException {
        JAXBContext jaxbContext = getJaxbContext(objClass);
        return createMarshaller(jaxbContext);
    }

    private Unmarshaller getUnMarshaller(Class<?> objClass) throws JAXBException {
        JAXBContext jaxbContext = getJaxbContext(objClass);
        return createUnmarshaller(jaxbContext);
    }

    private Unmarshaller createUnmarshaller(JAXBContext jaxbContext) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller;
    }

    private JAXBContext getJaxbContext(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = jaxbContextMap.get(clazz);
        if (jaxbContext != null) {
            return jaxbContext;
        }
        jaxbContext = JAXBContext.newInstance(clazz);
        jaxbContextMap.put(clazz, jaxbContext);
        return jaxbContext;
    }

    private Marshaller createMarshaller(JAXBContext jaxbContext) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
