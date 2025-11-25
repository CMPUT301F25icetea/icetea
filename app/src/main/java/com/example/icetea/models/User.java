package com.example.icetea.models;

public class User {
    private String id;
    private String email;
    private String name;
    private String phone;
    private boolean notifications;
    private String avatar;

    /**
     * Default constructor required by Firestore and serialization frameworks.
     */
    public User() {
        // Required empty constructor
    }

    /**
     * Returns the user's ID.
     *
     * @return User ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id User ID
     */
    public void setId(String id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
