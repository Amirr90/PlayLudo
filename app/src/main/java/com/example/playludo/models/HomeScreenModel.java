package com.example.playludo.models;

public class HomeScreenModel {
    int image;
    String title;

    public HomeScreenModel(int image, String title) {
        this.image = image;
        this.title = title;
    }

    public HomeScreenModel() {
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
