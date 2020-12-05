package com.example.myapp;

public class NewsStore {
    String[] url;
    String[] title;
    String[] img;
    String[] time;
    public NewsStore()
    {

    }
    public NewsStore(String[] url, String[] img, String[] title,String[] time) {
        this.url = url;
        this.img = img;
        this.title = title;
        this.time=time;
    }


    public String[] getTime() {
        return time;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
        this.url = url;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public String[] getTitle() {
        return title;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }
}
