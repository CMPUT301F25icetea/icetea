package com.example.icetea.common;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

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

    /**
     * Loads the current user's profile from the database and populates
     * the given EditText fields.
     *
     * @param firstName EditText for first name
     * @param lastName EditText for last name
     * @param email EditText for email
     * @param phone EditText for phone number
     * @param callback Callback to signal success or failure
     */
    public void loadProfile(EditText firstName, EditText lastName, EditText email, EditText phone, Callback<Void> callback) {
        UserDB.getInstance().getUser(FBAuthenticator.getCurrentUserId(), task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    firstName.setText(document.getString("firstName"));
                    lastName.setText(document.getString("lastName"));
                    email.setText(document.getString("email"));
                    phone.setText(document.getString("phone"));
                    callback.onSuccess(null);
                }
            } else {
                callback.onFailure(new Exception("Could not load profile"));
            }
        });
    }

    /**
     * Updates the current user's profile data in Firebase Auth and Firestore.
     *
     * @param firstName EditText for first name
     * @param lastName EditText for last name
     * @param email EditText for email
     * @param phone EditText for phone number
     * @param callback Callback to signal success or failure
     */
    public void updateProfile(EditText firstName, EditText lastName, EditText email, EditText phone, Callback<Void> callback) {
        //TODO: issue with firebase enforcing emails to be verified before changed.
        // here i just change it in our DB regardless, but the user still signs in with the old one till new is updated
        // so fb auth and our db are not synced
        // to fix: only update after user confirms, idk the best way yet

        // Extract trimmed values
        String firstNameString = firstName.getText().toString().trim();
        String lastNameString = lastName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();

        // Validate email and phone
        String errorMessage = validateInfo(emailString, phoneString);
        if (errorMessage != null) {
            callback.onFailure(new Exception(errorMessage));
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstNameString);
        updates.put("lastName", lastNameString);
        updates.put("email", emailString);
        updates.put("phone", phoneString);

        // Update Firebase Auth first for safety
        FBAuthenticator.updateUser(emailString, task -> {
            if (task.isSuccessful()) {
                // Update Firestore DB
                UserDB.getInstance().updateUser(FBAuthenticator.getCurrentUserId(), updates, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception("Failed updating in firestore DB"));
                    }
                });
            } else {
                callback.onFailure(new Exception("Couldn't add new email to FB Auth"));
            }
        });
    }

    /**
     * Validates email and phone input formats.
     *
     * @param email The email string to validate
     * @param phone The phone string to validate
     * @return Error message if invalid, otherwise null
     */
    private String validateInfo(String email, String phone) {
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format";
        }

        if (phone != null && !phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            return "Invalid phone format";
        }

        return null;
    }

    /**
     * Deletes the current user's profile from both Firebase Auth and Firestore DB.
     *
     * @param callback Callback to signal success or failure
     */
    public void deleteProfile(Callback<Void> callback) {
        // delete from both our DB and from fb auth - auth first for safety
        //TODO: Delete the user from all the waitlists they are on? cancel their invitations? etc...
        //TODO: Deal with FirebaseAuthRecentLoginRequiredException better?
        String id = FBAuthenticator.getCurrentUserId(); // save id before deletion

        FBAuthenticator.deleteUser(task -> {
            if (task.isSuccessful()) {
                UserDB.getInstance().deleteUser(id, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception("Couldn't delete from FireStore DB"));
                    }
                });
            } else {
                callback.onFailure(new Exception("Couldn't delete from FB Auth"));
            }
        });
    }

    /**
     * Logs out the current user from Firebase Auth.
     */
    public void logoutUser() {
        FBAuthenticator.logout();
    }
}
