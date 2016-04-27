package com.mind.httpclient.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.mind.httpclient.jackson.JsonUtils;
import com.mind.httpclient.jackson.XmlUtils;
import com.mind.httpclient.net.NetUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Asserts;

import java.util.*;

/**
 * Created by serv on 2015/3/20.
 */
public abstract class FormCommand extends Command{

    public FormCommand(String command) {
        super(command);
    }

    @Override
    protected void doExecute() throws Exception {
        Location location = getLocation();
        Map<String, String> formVariables = getFormVariables();
        log.debug("post {}\n{}",location.getUrl(),formVariables.toString());
        NetUtils.setCharsetEncoding(getCharsetEncoding());
        String resultText = NetUtils.post(location.getUrl(), new UrlEncodedFormEntity(map2Pair(formVariables), getCharsetEncoding()),getHttpClient());
        log.debug("return {}",resultText);
        Asserts.notEmpty(resultText, "resultText");

        JsonNode jsonNode = wrap2JsonNode(resultText);

        afterExecuted(jsonNode,resultText);

    }

    protected String getCharsetEncoding(){
        return "utf-8";
    }

    protected abstract Map<String, String> getFormVariables() ;

    protected List<NameValuePair> map2Pair(Map<String,String> map){
        Set<String> strings = map.keySet();
        Iterator<String> iterator = strings.iterator();
        List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            valuePairs.add(new BasicNameValuePair(key,value));
        }
        return valuePairs;
    }

    protected JsonNode wrap2JsonNode(String resultText){
        try{
            if(resultText.trim().startsWith("<")){
                return  XmlUtils.xml2Obj(resultText, JsonNode.class);
            }else if(resultText.trim().startsWith("{")){
                return  JsonUtils.json2Object(resultText, JsonNode.class);
            }
            return null;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 当解析返回json或者xml出错。或者返回text非上述格式。则 jsonNode 为 null
     */
    protected void afterExecuted(JsonNode jsonNode,String resultText) {

    }
}
