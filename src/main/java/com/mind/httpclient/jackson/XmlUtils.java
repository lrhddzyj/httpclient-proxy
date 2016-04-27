package com.mind.httpclient.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by serv on 2015/3/16.
 */
public class XmlUtils {

    private final static XmlMapper xmlMapper;

    static {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static  <T> T xml2Obj(String xmlContent,Class<T> tClass){
        try {
            if(tClass.isAssignableFrom(String.class)){
                return (T) xmlContent;
            }
            return xmlMapper.readValue(xmlContent,tClass);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public static  <T> T stream2Obj(InputStream content,Class<T> tClass){
        try {
            return xmlMapper.readValue(content,tClass);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static byte[] obj2byte(Object object){
        try {
            return xmlMapper.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String obj2String(Object message){

        ObjectWriter objectWriter = xmlMapper.writerWithType(message.getClass());
        try {
            return objectWriter.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
