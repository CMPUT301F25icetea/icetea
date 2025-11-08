package com.example.icetea.auth;

import android.util.Patterns;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;

/**
 * Controller responsible for handling user sign-up logic.
 *
 * Validates input fields, interacts with Firebase authentication to create a new user,
 * and saves the user to the database via UserDB.
 */
public class SignUpController {

    /**
     * Validates user input for sign-up.
     *
     * @param email User's email address.
     * @param password User's chosen password.
     * @param role User's selected role.
     * @return Error message if input is invalid, otherwise null if input is valid.
     */
    public String validateInput(String email, String password, String role) {
        if (email.isEmpty() || password.isEmpty()) {
            return "Email/Password cannot be empty";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (role == null || role.isEmpty()) {
            return "Role was not selected"; // shouldn't happen since its a radio button
        }
        return null;
    }

    /**
     * Signs up a new user using Firebase Authentication and saves the user to UserDB.
     * Uses nested callbacks to handle asynchronous operations.
     *
     * @param email User's email address.
     * @param password User's chosen password.
     * @param role User's selected role.
     * @param callback Callback to notify the caller of success or failure.
     */
    public void signUp(String email, String password, String role, Callback<Void> callback) {
        FBAuthenticator.signUpUser(email, password, task -> {
            if (task.isSuccessful()) {
                String id = FBAuthenticator.getCurrentUser().getUid();
                User newUser = new User(id, email, role);
                UserDB.getInstance().saveUser(newUser, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(dbtask.getException() != null ? dbtask.getException().getMessage() : "Unknown Error : SignUpController : DB FAIL"));
                    }
                });
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown Error : SignUpController : Auth Fail"));
            }
        });
    }
}
