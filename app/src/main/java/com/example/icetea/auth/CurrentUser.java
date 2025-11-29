package com.example.icetea.auth;

import android.graphics.Bitmap;

public class CurrentUser {
    private String fid;
    private String name;
    private String email;
    private String phone;
    private boolean notifications;
    private Bitmap avatar;

    private static CurrentUser instance;

    public static CurrentUser getInstance() {
        if (instance == null) instance = new CurrentUser();
        return instance;
    }

    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    public Bitmap getAvatar() {
        return avatar;
    }
    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

}