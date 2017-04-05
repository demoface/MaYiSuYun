package com.mayikeji.shoujibaidu.bean;

/**
 * author : solon
 * date: on 16/12/15.
 */

public class UpdateBean {
    private String up ;
    private String url ;
    private String now ;
    private String last ;

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
