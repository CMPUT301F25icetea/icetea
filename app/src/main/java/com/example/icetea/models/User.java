package com.example.icetea.models;

/**
 * Represents a user in the system.
 * Contains basic information such as ID, email, role, and optional first and last names.
 */
public class User {
    private String id;
    private String email;

    private String name;
    private String phone;
    private boolean notificationsEnabled;

    /**
     * Default constructor required by Firestore and serialization frameworks.
     */
    public User() {
        // Required empty constructor
    }

    /**
     * Constructor to create a user with ID, email, and role.
     *
     * @param id The unique user ID
     * @param email The user's email address
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    /**
     * Returns the user's email.
     *
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email Email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
