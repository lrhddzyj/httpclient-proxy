package com.mind.httpclient.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serv on 2015/3/17.
 */
public final class Executors {

    public static ExecutorBuilder build(){
        return new ExecutorBuilder();
    }

    public static class ExecutorBuilder{

        private Map<String,String> variables = new HashMap<String, String>();

        public ExecutorBuilder addVariable(String key,String value){
            variables.put(key,value);
            return this;
        }

        public <T extends Command> T exec(T t){
            t.addVariables(variables);
            t.execute();
            return t;
        }

    }

}
