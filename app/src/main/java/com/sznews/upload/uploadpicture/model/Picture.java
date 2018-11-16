package com.sznews.upload.uploadpicture.model;

/**
 * Created by qiy on 2018-1-24.
 */

public class Picture {
    private int picid;
    private String title;
    private String pic_path;
    private int dutyid;
    private int state;

    public Picture(String pic_path){
        setPic_path(pic_path);
    }

    public Picture(String title, String pic_path){
        setTitle(title);
        setPic_path(pic_path);
    }

    public Picture(int picid,String title, String pic_path,int dutyid,int state){
        setPicid(picid);
        setTitle(title);
        setPic_path(pic_path);
        setDutyid(dutyid);
        setState(state);
    }

    public int getPicid(){ return picid; }

    public void setPicid(int picid){ this.picid = picid; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }

    public int getDutyid() {
        return dutyid;
    }

    public void setDutyid(int dutyid) {
        this.dutyid = dutyid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
