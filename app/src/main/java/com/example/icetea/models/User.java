package com.example.icetea.models;

/**
 * Model class representing a User in the application.
 * <p>
 * This class maps to documents in the Firestore 'users' collection.
 * Each user document stores basic profile information, notification preferences,
 * and an optional avatar.
 * </p>
 *
 * <p>Firestore Collection Path: /users/{userId}</p>
 */
public class User {

    /** Unique identifier for the user (Firestore document ID) */
    private String id;

    /** User's email address */
    private String email;

    /** User's display name */
    private String name;

    /** User's phone number (optional) */
    private String phone;

    /** Flag indicating if the user has enabled notifications */
    private boolean notifications;

    /** Base64-encoded string representing the user's avatar image */
    private String avatar;

    /**
     * Default constructor required by Firestore and serialization frameworks.
     */
    public User() {
        // Required empty constructor
    }

    /**
     * Returns the user's unique ID.
     *
     * @return User ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's unique ID.
     *
     * @param id User ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the user's display name.
     *
     * @return Name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's display name.
     *
     * @param name Name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's email address.
     *
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's phone number.
     *
     * @return Phone number, may be null or empty
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone Phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns whether notifications are enabled for this user.
     *
     * @return True if notifications are enabled, false otherwise
     */
    public boolean getNotifications() {
        return notifications;
    }

    /**
     * Sets the notification preference for the user.
     *
     * @param notifications True to enable notifications, false to disable
     */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    /**
     * Returns the Base64-encoded avatar string for the user.
     *
     * @return Avatar as a Base64 string, or null if not set
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets the user's avatar.
     *
     * @param avatar Base64-encoded avatar string
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}