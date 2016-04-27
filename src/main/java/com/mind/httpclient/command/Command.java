package com.mind.httpclient.command;

import com.mind.httpclient.net.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by serv on 2015/3/16.
 */
public abstract class Command implements Serializable{

    protected Logger log = LoggerFactory.getLogger(getClass());

    private Location location;
    private String command;

    /**
     * 变量kv
     */
    private Map<String,String> variables = new HashMap<String, String>();

    /**
     * 指定命令名 对应command.xml中location节点的id
     * @param command
     */
    public Command(String command) {
        this.command = command;
    }


    private void initProperties() {
        if(command==null){
            return;
        }
        if(location==null){
            location = LocationLoader.getLocation(command);
        }
        location.setUrl(wrapVariables(location.getUrl()));
        location.setPostdata(wrapVariables(location.getPostdata()));

    }

    public final void addVariables(Map<String, String> variables) {
        Asserts.notNull(variables, "variables");
        this.variables.putAll(variables);
    }

    public final void addVariable(String key,String value){
        variables.put(key, value);
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * 执行请求
     */
    final void execute(){
        initProperties();
        try{
            doExecute();
        }catch (Exception e){
            throwGlobException(e);
        }
    }


    //need to override
    protected void throwGlobException(Throwable e){
        if(e instanceof RuntimeException){
            throw (RuntimeException)e;
        }else{
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    protected HttpClient getHttpClient() {
        return NetUtils.getHttpClient();
    }

    protected abstract void doExecute() throws Exception;

    private String wrapVariables(String text){
        if(StringUtils.isNotEmpty(text)){
            text = text.replace(" ","").replace("\n","");
            Set<String> keys = variables.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = variables.get(key);
                text = StringUtils.replace(text,"{"+key+"}",value);
            }
        }
        return text;
    }


}
