package com.sznews.upload.uploadpicture.model;

/**
 * Created by qiy on 2018-1-24.
 */

public class Picture {
    private String title;
    private String desc;
    private String pic_path;

    public Picture(String pic_path){
        setPic_url(pic_path);
    }

    public Picture(String title, String pic_path){
        setTitle(title);
        setPic_url(pic_path);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getpic_path() {
        return pic_path;
    }

    public void setPic_url(String pic_path) {
        this.pic_path = pic_path;
    }

}
