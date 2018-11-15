package com.sznews.upload.uploadpicture.model;

public class UploadTheme {
    private String theme;
    private String date;
    private String path;
    private String username;
    private int userid;
    private String description;
    private String category1;
    private String category2;
    private String source;
    private int dutyid;
    private int sum;
    private int num;
    private int state;

    public UploadTheme(){

    }

    public UploadTheme(String theme,String date,String path,String username,int userid,String description,String category1,String category2,int dutyid,int sum,int num,int state){
        setTheme(theme);
        setDate(date);
        setPath(path);
        setUsername(username);
        setUserid(userid);
        setDescription(description);
        setCategory1(category1);
        setCategory2(category2);
        setDutyid(dutyid);
        setSum(sum);
        setNum(num);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getDutyid() {
        return dutyid;
    }

    public void setDutyid(int dutyid) {
        this.dutyid = dutyid;
    }

    public int getSum(){
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
