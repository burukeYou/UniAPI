package com.burukeyou.uniapi.support.map;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class StringHashIMap extends HashMap<String,Object> implements IMap<String,Object> {

    public StringHashIMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public StringHashIMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringHashIMap() {
    }

    public StringHashIMap(Map<? extends String, ?> m) {
        super(m);
    }

    @Override
    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    @Override
    public Integer getInteger(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return ((Integer) value);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            if (str.indexOf('.') != -1) {
                return (int) Double.parseDouble(str);
            }
            return Integer.parseInt(str);
        }

        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Integer");
    }

    @Override
    public Boolean getBoolean(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            if("true".equalsIgnoreCase(str)){
                return true;
            }
            if ("false".equalsIgnoreCase(str)){
                return false;
            }
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Boolean");
    }

    @Override
    public Long getLong(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return ((Long) value);
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            return Long.valueOf(str);
        }

        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Long");
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value);
        }
        if (value instanceof String) {
            return new BigDecimal((String)value);
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        throw new ClassCastException("Can not cast '" + value.getClass() + "' to BigDecimal");
    }

    @Override
    public Double getDouble(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            return Double.valueOf(str);
        }

        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Double");
    }

    @Override
    public Float getFloat(String key) {
        Object value = super.get(key);

        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            return Float.valueOf(str);
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Float");
    }

    @Override
    public Short getShort(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Short) {
            return (Short) value;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            return Short.parseShort(str);
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Short");
    }

    @Override
    public Byte getByte(String key) {
        Object value = super.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
                return null;
            }
            return Byte.parseByte(str);
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to Byte");
    }

    @Override
    public byte[] getByteArr(String key) {
        return (byte[]) get(key);
    }

    @Override
    public Date getDate(String key) {
        return (Date) get(key);
    }

    @Override
    public LocalDate getLocalDate(String key) {
        return (LocalDate) get(key);
    }

    @Override
    public LocalDateTime getLocalDateTime(String key) {
        return (LocalDateTime) get(key);
    }

    @Override
    public File getFile(String key) {
        return (File) get(key);
    }

    @Override
    public InputStream getInputStream(String key) {
        return (InputStream) get(key);
    }

    @Override
    public <T> T getObject(String key) {
        return (T)get(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> objClass) {
        return (T)get(key);
    }

    @Override
    public <T> List<T> getList(String key) {
       return getList(key, null);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> objClass) {
        Object value = get(key);
        if(value == null){
            return null;
        }
        if(List.class.isAssignableFrom(value.getClass())){
            return (List<T>)value;
        }
        if (value.getClass().isArray()){
            return Arrays.asList((T[]) value);
        }
        if (Collection.class.isAssignableFrom(value.getClass())) {
            return new ArrayList<>((Collection) value);
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to List");
    }

    @Override
    public <T> T[] getArray(String key) {
        Object value = get(key);
        if(value == null){
            return null;
        }
        if (value.getClass().isArray()){
            return (T[]) value;
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to array");
    }

    @Override
    public <T> T[] getArray(String key, Class<T> objClass) {
        Object value = get(key);
        if(value == null){
            return null;
        }
        if (value.getClass().isArray()){
            return (T[]) value;
        }
        if(List.class.isAssignableFrom(value.getClass())){
            return  ((List<T>)value).toArray((T[]) Array.newInstance(objClass, 0));
        }
        if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection<T> valueCol = (Collection<T>)value;
            return valueCol.toArray((T[]) Array.newInstance(objClass, 0));
        }
        throw new ClassCastException("Can not cast '" + value.getClass() + "' to array");
    }
}
