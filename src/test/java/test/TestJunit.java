package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.mind.httpclient.command.Executors;
import com.mind.httpclient.command.JacksonCommand;
import org.junit.Test;

/**
 * Created by serv on 2015/3/18.
 */
public class TestJunit {

    @Test
    public void test01(){
        Executors.build().addVariable("accessToken","sdjf").exec(new JacksonCommand("baidu_get_test"){
            @Override
            protected void afterExecuted(JsonNode jsonNode, String resultText) {
                System.out.println(jsonNode.toString());
            }
        });
    }

    @Test
    public void test02(){
        Executors.build().addVariable("accessToken","sdjf").exec(new JacksonCommand("unifiedorder"){
            @Override
            protected void afterExecuted(JsonNode jsonNode,String resultText) {
                System.out.println(jsonNode.toString());
            }
        });
    }
}
