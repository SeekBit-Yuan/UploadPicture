package com.sznews.upload.uploadpicture.model;

public class UploadTheme {
    private String theme;
    private String date;
    private String path;
    private String state;

    public UploadTheme(String theme,String date,String path,String state){
        setTheme(theme);
        setDate(date);
        setPath(path);
        setState(state);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
