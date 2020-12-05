package com.example.myapp;

public class poi_storage {

    String position;
    String distance;
    String title;
    int url;

    public poi_storage() {
    }

    public poi_storage(String position, String distance, String title, int url) {
        this.position = position;
        this.distance = distance;
        this.title = title;
        this.url=url;
    }

    public int getUrl() {
        return url;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
