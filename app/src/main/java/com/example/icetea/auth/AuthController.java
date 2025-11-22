package com.example.icetea.auth;

import android.util.Patterns;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.units.qual.C;

public class AuthController {

    public void signUp(String name, String email, Callback<Void> callback) {
        FBI.getCurrentFID(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fid = task.getResult();

                User newUser = new User();
                newUser.setId(fid);
                newUser.setName(name);
                newUser.setEmail(email);

                UserDB.getInstance().saveUser(newUser, dbtask -> {
                    if (dbtask.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        Exception e = dbtask.getException() != null
                                ? dbtask.getException()
                                : new Exception("Failed to save user");
                        callback.onFailure(e);
                    }
                });

            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to get FID");
                callback.onFailure(e);
            }
        });
    }

    public void userExists(Callback<Boolean> callback) {
        // Get current FID
        FBI.getCurrentFID(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fid = task.getResult();

                // Check if user exists in Firestore
                UserDB.getInstance().getUser(fid, userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                        DocumentSnapshot doc = userTask.getResult();
                        callback.onSuccess(doc.exists());
                    } else {
                        Exception e = userTask.getException() != null
                                ? userTask.getException()
                                : new Exception("Failed to check user");
                        callback.onFailure(e);
                    }
                });

            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to get FID");
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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }
}
