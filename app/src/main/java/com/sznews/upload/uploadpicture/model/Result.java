package com.sznews.upload.uploadpicture.model;

/**
 * Author:hytao
 * Time:2018/10/18
 * Descrrption:返回类，用于存放返回信息
 */
public class Result {
    private String state;
    private String msg;
    private String token;
    private String dutyid;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDutyid(){ return dutyid;}

    public void setDutyid(String dutyid) {
        this.dutyid = dutyid;
    }
}
