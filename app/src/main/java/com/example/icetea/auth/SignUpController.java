package com.example.icetea.auth;

import android.util.Patterns;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;

public class SignUpController {
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

    //nested callbacks, mayhaps will edit later
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
