package com.mpetroiu.smc_admin;

public class Upload {

    private String Location;
    private String Thumbnail;

    public Upload(){
    }

    public Upload(String location, String thumbnail) {
        Location = location;
        Thumbnail = thumbnail;
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


}
