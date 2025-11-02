package com.example.icetea.auth;

import android.util.Log;
import android.util.Patterns;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;

public class SignUpController {

    public interface SignUpCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }

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

    public void signUp(String email, String password, String role, SignUpCallback callback) {
        FBAuthenticator.signUpUser(email, password, task -> {
            if (task.isSuccessful()) {

                String id = FBAuthenticator.getCurrentUser().getUid();
                User newUser = new User(id, email, role);
                UserDB.getInstance().saveUser(newUser, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Passed authentication; Sign up failed from FBdb side");
                    }
                });

            } else {

                String error = "Signup failed";
                if (task.getException() != null) {
                    error = task.getException().getMessage();
                    Log.e("SignUpController", "Sign up failed from FBAuth side", task.getException());
                }

                callback.onFailure(error);
            }
        });
    }
}
