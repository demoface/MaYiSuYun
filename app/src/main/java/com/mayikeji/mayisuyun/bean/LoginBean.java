package com.mayikeji.mayisuyun.bean;

/**
 * author : solon
 * date: on 16/12/15.
 */

public class LoginBean {
    private String uid ;
    private String status ;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
