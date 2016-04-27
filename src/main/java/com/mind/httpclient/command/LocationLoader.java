package com.mind.httpclient.command;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mind.httpclient.jackson.XmlUtils;
import org.apache.http.util.Asserts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by serv on 2014/8/4.
 */
class LocationLoader {

    private static Map<String,Location> locations = null;

    static{
        locations = new HashMap<String, Location>();

        XmlMapper xmlMapper = null;
        try {
            xmlMapper = XmlUtils.getXmlMapper();
            CollectionType collectionType = xmlMapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);
            InputStream resourceAsStream = LocationLoader.class.getClassLoader().getResourceAsStream("command.xml");
//            InputStream resourceAsStream = Files.newInputStream(new File("c:/command.xml").toPath());//"template/command.xml");
            Collection<HashMap> list = xmlMapper.readValue(resourceAsStream, collectionType);
            for(HashMap map : list){
                Location location = new Location((String)map.get("url"),(String)map.get("postdata"));
                locations.put((String)map.get("id"),location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getLocation(String command){
        try{
            Location location = (Location) locations.get(command).clone();
            Asserts.notNull(location,"location");
            return location;
        }catch (Exception e){
            throw new RuntimeException("command命令未找到,请检查command.xml");
        }
     }
}
