package com.example.joellehanna.libraryuser;

import android.net.Uri;

public class User {

    public String username;
    public Uri profilepic;

    public String getUsername() {
        return username;
    }

    public Uri getProfilepic() {
        return profilepic;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilepic(Uri profilepic) {
        this.profilepic = profilepic;
    }
}
