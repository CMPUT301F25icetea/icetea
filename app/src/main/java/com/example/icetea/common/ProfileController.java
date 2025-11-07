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

public class ProfileController {
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

    public void updateProfile(EditText firstName, EditText lastName, EditText email, EditText phone, Callback<Void> callback) {
        //TODO: issue with firebase enforcing emails to be verified before changed.
        // here i just change it in our DB regardless, but the user still signs in with the old one till new is updated
        // so fb auth and our db are not synced
        // to fix: only update after user confirms, idk the best way yet

        String firstNameString = firstName.getText().toString().trim();
        String lastNameString = lastName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();

        // firstName, lastName, phone not required
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

        // update both in FBAuthenticator (for email changes) and in user database - auth first for safety
        FBAuthenticator.updateUser(emailString, task -> {
            if (task.isSuccessful()) {
                // update in our db
                UserDB.getInstance().updateUser(FBAuthenticator.getCurrentUserId(), updates, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception("Failed updating in firestore DB")); //but it updated in fb auth
                    }
                });
            } else {
                callback.onFailure(new Exception("Couldn't add new email to FB Auth"));

            }
        });

    }

    private String validateInfo(String email, String phone) {
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format";
        }

        if (phone != null && !phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            return "Invalid phone format";
        }

        return null;
    }


    public void deleteProfile(Callback<Void> callback) {
        // delete from both our DB and from fb auth - auth first for safety
        //TODO: Delete the user from all the waitlists they are on? cancel their invitations? etc...
        //TODO: Deal with FirebaseAuthRecentLoginRequiredException better?

        String id = FBAuthenticator.getCurrentUserId(); // save it since we about to delete

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

    public void logoutUser() {
        FBAuthenticator.logout();
    }
}