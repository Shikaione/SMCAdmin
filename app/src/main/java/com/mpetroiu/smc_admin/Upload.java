package com.mpetroiu.smc_admin;

import com.google.firebase.database.Exclude;

public class Upload {

    private String Location;
    private String Thumbnail;
    private String Key;

    public Upload(){
    }

    public Upload(String location, String thumbnail, String key) {
        Location = location;
        Thumbnail = thumbnail;
        Key = key;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }
}
