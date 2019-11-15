package com.example.usmansh.findmycar;

class Tracking {



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Tracking( String lat, String lang) {
        this.lat = lat;
        this.lang = lang;
    }

    public Tracking(){

    }
    String lat;
    String lang;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;


}
