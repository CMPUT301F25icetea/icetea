package com.example.icetea.auth;

import android.util.Log;
import android.util.Patterns;

import com.example.icetea.util.Callback;

public class LoginController {

    public String validateInput(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return "Email/Password cannot be empty";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid Email Format";
        }
        return null;
    }

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
