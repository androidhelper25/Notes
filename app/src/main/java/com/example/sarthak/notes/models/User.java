package com.example.sarthak.notes.models;

import android.net.Uri;

public class User {

    public String profileImage;
    public String profileName;
    public String profileEmail;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(Uri profileImage, String profileName, String profileEmail) {

        this.profileImage = profileImage.toString();
        this.profileName = profileName;
        this.profileEmail = profileEmail;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileEmail() {
        return profileEmail;
    }
}
