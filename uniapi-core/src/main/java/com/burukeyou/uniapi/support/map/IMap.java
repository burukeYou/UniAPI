package com.burukeyou.uniapi.support.map;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * On top of the original {@link java.util.Map}, some built-in force-conversion methods are provided
 * @param <K>
 * @param <V>
 */
public interface IMap<K, V> extends java.util.Map<K, V> {

    /**
     * get cast to String
     * @param key       param key
     * @return          string value
     */
    K getString(K key);

    /**
     * get cast to Integer
     * @param key       param key
     * @return          integer value
     */
    Integer getInteger(K key);

    /**
     * get cast to Boolean
     * @param key       param key
     * @return          boolean value
     */
    Boolean getBoolean(K key);

    /**
     * get cast to Long
     * @param key       param key
     * @return          long value
     */
    Long getLong(K key);

    /**
     * get cast to BigDecimal
     * @param key       param key
     * @return          BigDecimal value
     */
    BigDecimal getBigDecimal(K key);

    /**
     * get cast to Double
     * @param key       param key
     * @return          Double value
     */
    Double getDouble(K key);

    /**
     * get cast to Float
     * @param key       param key
     * @return          Float value
     */
    Float getFloat(K key);

    /**
     * get cast to Short
     * @param key       param key
     * @return          Short value
     */
    Short getShort(K key);

    /**
     * get cast to Byte
     * @param key       param key
     * @return          Byte value
     */
    Byte getByte(K key);

    /**
     * get cast to  byte array
     * @param key       param key
     * @return          byte array value
     */
    byte[] getByteArr(K key);

    /**
     * get cast to Date
     * @param key       param key
     * @return          Date value
     */
    Date getDate(K key);

    /**
     * get cast to LocalDate
     * @param key       param key
     * @return          LocalDate value
     */
    LocalDate getLocalDate(K key);

    /**
     * get cast to LocalDateTime
     * @param key       param key
     * @return          LocalDateTime value
     */
    LocalDateTime getLocalDateTime(K key);

    /**
     * get cast to File
     * @param key       param key
     * @return          File value
     */
    File getFile(K key);

    /**
     * get cast to InputStream
     * @param key       param key
     * @return          InputStream value
     */
    InputStream getInputStream(K key);

    /**
     * get cast to Object
     * @param key       param key
     * @return          Object value
     */
    <T> T getObject(K key);

    /**
     * get cast to Object
     * @param key       param key
     * @return          Object value
     */
    <T> T getObject(K key, Class<T> objClass);

    /**
     * get cast to Object List
     * @param key       param key
     * @return          List value
     */
    <T> List<T> getList(K key);

    /**
     * get cast to Object List
     * @param key       param key
     * @return          List value
     */
    <T> List<T> getList(K key, Class<T> objClass);

    /**
     * get cast to Object array
     * @param key       param key
     * @return          array value
     */
    <T> T[] getArray(K key);

    /**
     * get cast to Object array
     * @param key       param key
     * @return          array value
     */
    <T> T[] getArray(K key, Class<T> objClass);


}
