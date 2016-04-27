package com.mind.httpclient.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.mind.httpclient.jackson.JsonUtils;
import com.mind.httpclient.jackson.XmlUtils;
import com.mind.httpclient.net.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

/**
 * 仅支持 json 或者 xml 格式的数据交互
 * Created by serv on 2015/3/18.
 */
public abstract class JacksonCommand extends Command {

    public JacksonCommand(String command) {
        super(command);
    }

    @Override
    protected void doExecute() throws Exception {
        Location location = getLocation();
        String resultText = null;
        NetUtils.setCharsetEncoding(getCharsetEncoding());
        //没有定义postData 则为get请求
        if(StringUtils.isEmpty(location.getPostdata())){
            log.debug("get {}",location.getUrl());
            resultText = NetUtils.get(location.getUrl());
        }else{
            log.debug("post {}\n{}",location.getUrl(),location.getPostdata());
            resultText = NetUtils.post(location.getUrl(), location.getPostdata(),
                    location.getPostdata().startsWith("{")?"application/json":"text/xml",getHttpClient());
        }
        log.debug("return {}",resultText);
        Asserts.notEmpty(resultText, "resultText");

        JsonNode jsonNode = wrap2JsonNode(resultText);

        afterExecuted(jsonNode, resultText);

    }

    protected String getCharsetEncoding(){
        return "utf-8";
    }


    protected JsonNode wrap2JsonNode(String resultText){
        if(resultText.trim().startsWith("<")){
            return  XmlUtils.xml2Obj(resultText, JsonNode.class);
        }else if(resultText.trim().startsWith("{")){
            return  JsonUtils.json2Object(resultText, JsonNode.class);
        }else{
            throw new RuntimeException("不是json或者xml格式");
        }
    }

    protected void afterExecuted(JsonNode jsonNode,String resultText) {

    }
}
