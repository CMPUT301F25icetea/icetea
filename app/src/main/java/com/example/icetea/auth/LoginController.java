package com.example.icetea.auth;

import android.util.Log;
import android.util.Patterns;

public class LoginController {

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public String validateInput(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return "Email/Password cannot be empty";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid Email Format";
        }
        return null;
    }

    public void login(String email, String password, LoginCallback callback) {
        FBAuthenticator.loginUser(email, password, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                String error = "Authentication failed";
                if (task.getException() != null) {
                    error = task.getException().getMessage();
                    Log.e("LoginController", "Login failed", task.getException());
                }
                callback.onFailure(error);
            }
        });
    }
}
