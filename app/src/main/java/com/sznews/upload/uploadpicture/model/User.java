package com.sznews.upload.uploadpicture.model;

import java.util.Date;
import java.util.Random;

/**
 * Author:hytao
 * Time:2018/10/18
 * Descrrption:用户类，用于存放用户的各种信息
 */
public class User {
    private String username;
    private String userpass;
    private String token;
    private String appid = "d2a57dc1d883fd21fb9951699df71cc7";
    private String scode;
    private String timestamp = String.valueOf(new Date().getTime());
    private String nonce = getRandomNum();
    //签名、时间戳、随机数、用户名、用户密码、appid

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    //获取六位随机数
    private String getRandomNum(){
        Random random = new Random();
        StringBuffer result = new StringBuffer();
        for (int i=0;i<6;i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public String getSignature() {
        return scode;
    }

    public void setSignature(String signature) {
        this.scode = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

}
