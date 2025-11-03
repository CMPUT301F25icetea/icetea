package com.example.icetea.models;

import com.example.icetea.auth.FBAuthenticator;

public class User {
    private String id, email, role;

    public User() {
        //req
    }

    public User(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
