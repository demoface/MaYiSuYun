package com.mayikeji.mayisuyun.bean;

/**
 * author : solon
 * date: on 16/10/13.
 */

public class Result<T> {
    protected int code	;
    protected String info	;
    protected T data ;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}

