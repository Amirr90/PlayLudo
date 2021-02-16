package com.example.playludo.models;

public class User {

    String name;
    String uid;
    long credits;
    long invest;
    long earn;
    String image;
    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getInvest() {
        return invest;
    }

    public void setInvest(long invest) {
        this.invest = invest;
    }

    public long getEarn() {
        return earn;
    }

    public void setEarn(long earn) {
        this.earn = earn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
