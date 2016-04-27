package com.mind.httpclient.command;

import java.io.Serializable;

/**
 * Created by serv on 2014/8/1.
 */
public class Location implements Cloneable,Serializable{
    private String url;
    private String postdata;

    public Location(String url, String postdata) {
        this.url = url;
        this.postdata = postdata;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPostdata() {
        return postdata;
    }

    public void setPostdata(String postdata) {
        this.postdata = postdata;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}