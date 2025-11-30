package com.example.icetea.auth;

import android.graphics.Bitmap;

/**
 * Singleton class representing the currently authenticated user.
 * <p>
 * Holds user data such as Firebase Installation ID (FID), name, email, phone number,
 * notification preference, and avatar image.
 * <p>
 * This class ensures there is only one instance of the current user throughout the app.
 */
public class CurrentUser {

    /** Firebase Installation ID of the current user */
    private String fid;

    /** Full name of the current user */
    private String name;

    /** Email address of the current user */
    private String email;

    /** Phone number of the current user */
    private String phone;

    /** Whether the user has notifications enabled */
    private boolean notifications;

    /** Avatar image of the user as a Bitmap */
    private Bitmap avatar;

    /** Singleton instance of CurrentUser */
    private static CurrentUser instance;

    /**
     * Returns the singleton instance of {@link CurrentUser}.
     *
     * @return the single {@link CurrentUser} instance
     */
    public static CurrentUser getInstance() {
        if (instance == null) instance = new CurrentUser();
        return instance;
    }

    /** Returns the Firebase Installation ID of the current user */
    public String getFid() {
        return fid;
    }

    /** Sets the Firebase Installation ID of the current user */
    public void setFid(String fid) {
        this.fid = fid;
    }

    /** Returns the name of the current user */
    public String getName() {
        return name;
    }

    /** Sets the name of the current user */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the email of the current user */
    public String getEmail() {
        return email;
    }

    /** Sets the email of the current user */
    public void setEmail(String email) {
        this.email = email;
    }

    /** Returns the phone number of the current user */
    public String getPhone() {
        return phone;
    }

    /** Sets the phone number of the current user */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Returns whether notifications are enabled for the current user */
    public boolean getNotifications() {
        return notifications;
    }

    /** Sets whether notifications are enabled for the current user */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    /** Returns the avatar of the current user */
    public Bitmap getAvatar() {
        return avatar;
    }

    /** Sets the avatar of the current user */
    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
}