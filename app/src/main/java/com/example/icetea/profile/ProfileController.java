package com.example.icetea.profile;

import android.util.Patterns;
import android.widget.EditText;

import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

/**
 * Controller responsible for managing the user's profile data.
 *
 * Handles loading, updating, and deleting user information from both
 * Firebase Authentication and Firestore database.
 */
public class ProfileController {

    public void updateProfile(String fid, HashMap<String, Object> updates, Callback<Void> callback) {

        UserDB.getInstance().updateUser(fid, updates, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to update profile");
                callback.onFailure(e);
            }
        });
    }

    public String validateName(String name) {
        if (name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }

    public String validatePhone(String phone) {
        if (phone.isEmpty()) {
            return null;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            return "Phone number is invalid";
        }
        return null;
    }

    public void deleteProfile(String fid, Callback<Void> callback) {
        //delete events owned (and timers for notifications?)
        //delete all waitinglist entries
        //delete user profile
        //logs? log the deletion?
    }

}
