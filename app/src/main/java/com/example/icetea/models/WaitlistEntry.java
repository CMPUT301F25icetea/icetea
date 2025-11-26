package com.example.icetea.models;

import android.graphics.Bitmap;

public class WaitlistEntry {
    private String name;
    private String email;
    private String status;
    private Bitmap avatarBitmap;

    public WaitlistEntry(String name, String email, String status, Bitmap avatarBitmap) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.avatarBitmap = avatarBitmap;
    }

    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}