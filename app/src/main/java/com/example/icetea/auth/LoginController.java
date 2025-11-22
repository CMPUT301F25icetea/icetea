package com.example.icetea.auth;

import android.provider.Settings;
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
                if (task.getException() != null) {
                    callback.onFailure(task.getException());
                } else {
                    callback.onFailure(new Exception("Unknown Error : LoginController"));
                }
            }
        });
    }

    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }

    public String validatePasswordLogin(String password) {
        if (password.isEmpty()) {
            return "Password cannot be empty";
        }
        return null;
    }
}