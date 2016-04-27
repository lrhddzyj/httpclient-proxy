package com.mind.httpclient.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serv on 2015/3/16.
 */
public class JsonUtils {
    private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) ;
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }


    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * json转化成对象
     * @param jsonText json内容
     * @param targetType 转化类型
     * @param <T> 类型
     * @return
     */
    public static  <T> T json2Object(String jsonText , Class<T> targetType){
        T t = null;
        try {
            t = objectMapper.readValue(jsonText, targetType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("json转换异常\n%s\n%s",jsonText,targetType.getName()));
        }
        return t;
    }

    /**
     * json转化成list集合
     * @param jsonText
     * @param targetType
     * @param <T>
     * @return
     */
    public static  <T> List<T> json2List(String jsonText , Class<T> targetType){
        List<T> tList = null;
        try {
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, targetType);
            tList = objectMapper.readValue(jsonText, collectionType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("json转换异常\n%s\n%s",jsonText,targetType.getName()));
        }
        return tList;
    }

    /**
     * json转化成数组
     * @param jsonText
     * @param targetType
     * @param <T>
     * @return
     */
    public static  <T> T[] json2Array(String jsonText , Class<T> targetType){
        try {
            ArrayType arrayType = objectMapper.getTypeFactory().constructArrayType(targetType);
            return objectMapper.readValue(jsonText, arrayType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("json转换异常\n%s\n%s",jsonText,targetType.getName()));
        }
    }

    public static String object2Json(Object sourObject){
        try {
            return objectMapper.writeValueAsString(sourObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("json转换异常 %s",sourObject.toString()));
        }
    }

}
