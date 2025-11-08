package com.example.icetea.models;

/**
 * Represents a user in the system.
 * Contains basic information such as ID, email, role, and optional first and last names.
 */
public class User {
    private String id, email, role, firstName, lastName;

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
     * @param role The user's role (e.g., "entrant" or "organizer")
     */
    public User(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
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

    /**
     * Returns the user's role.
     *
     * @return Role string
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role Role string
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the user's first name.
     *
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName First name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName Last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
