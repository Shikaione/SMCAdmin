package com.mpetroiu.smc_admin;

public class Place {

    private String Location;
    private String Thumbnail;
    private String Owner;
    private String Type;
    private String Address;
    private String Phone;
    private String Email;
    private String User;

    public Place(){
    }

    public Place(String location, String thumbnail, String owner, String type,
                  String address, String phone, String email, String user) {
        Location = location;
        Thumbnail = thumbnail;
        Owner = owner;
        Type = type;
        Address = address;
        Phone = phone;
        Email = email;
        User = user;
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

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}
