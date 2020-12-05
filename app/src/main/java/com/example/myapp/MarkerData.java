package com.example.myapp;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;

public class MarkerData {
    MarkerOptions marker;
    String Address;
    String situation;


    public MarkerData(MarkerOptions marker, String address, String situation) {
        this.marker = marker;
        Address = address;
        this.situation = situation;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }
}
