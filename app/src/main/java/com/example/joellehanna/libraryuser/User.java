package com.example.joellehanna.libraryuser;

import android.net.Uri;

public class User {

    public String picture;
    public String username;

    public User() {
    }

    public User(String picture, String username) {
        this.picture = picture;
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}