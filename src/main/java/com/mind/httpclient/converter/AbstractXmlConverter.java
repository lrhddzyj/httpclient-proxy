package com.mind.httpclient.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mind.httpclient.jackson.XmlUtils;

import java.util.Map;

/**
 * Created by serv on 2014/7/23.
 */
public abstract class AbstractXmlConverter<T extends Object> {

    private Map<String,Class> types;

    public AbstractXmlConverter(Map<String, Class> types) {
        this.types = types;
    }

    public Map<String, Class> getTypes() {
        return types;
    }

    public <T> T converter(String wxContentXml){

        try {
            XmlMapper xmlMapper = XmlUtils.getXmlMapper();

            //读取xml 属性到 map
            JsonNode jsonNode = xmlMapper.readValue(wxContentXml, JsonNode.class);

            //根据消息类型获取对应的class类型
            Class messageClass = getClass(jsonNode,wxContentXml);

            if(messageClass==null){
                throw new RuntimeException("未找到对应的class类型，请检查types映射 xml内容："+wxContentXml);
            }
            //执行消息serializable
            return (T)xmlMapper.readValue(wxContentXml,messageClass);
        } catch(Exception e){
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }else{
                throw new RuntimeException("xml转换异常,请检types类型 : "+e.getMessage());
            }
        }


    }

    protected abstract Class getClass(JsonNode jsonNode,String xmlContent);



}
