package com.example.joellehanna.libraryuser;

import android.net.Uri;

public class User {

    public String firstname;
    public String lastname;
    public Uri profilepic;
    public String email;


    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public Uri getProfilepic() {
        return profilepic;
    }

    public String getemailAdress() {
        return email;
    }


}
