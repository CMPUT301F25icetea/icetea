package com.example.icetea.auth;

import android.util.Log;
import android.util.Patterns;

import com.example.icetea.util.Callback;

/**
 * Controller class for handling user login operations.
 *
 * Provides input validation and login logic using Firebase authentication.
 */
public class LoginController {

    /**
     * Validates the provided email and password input.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A string containing an error message if validation fails,
     *         or null if the input is valid.
     */
    public String validateInput(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return "Email/Password cannot be empty";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid Email Format";
        }
        return null;
    }

    /**
     * Attempts to log in a user with the given email and password.
     *
     * Calls the provided Callback with success if login succeeds, or failure if it fails.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param callback The callback to invoke upon success or failure.
     */
    public void login(String email, String password, Callback<Void> callback) {
        FBAuthenticator.loginUser(email, password, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown Error : LoginController"));
            }
        });
    }
}
