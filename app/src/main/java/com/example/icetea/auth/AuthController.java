package com.example.icetea.auth;

import android.util.Patterns;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Controller class responsible for handling authentication-related operations.
 * <p>
 * Handles user sign-up, checking if a user exists, and validation of user input.
 */
public class AuthController {

    /**
     * Signs up a new user.
     * <p>
     * Retrieves the current Firebase Installation ID (FID), creates a new {@link User} object,
     * populates the {@link CurrentUser} singleton, and saves the user to Firestore.
     *
     * @param name     the user's full name
     * @param email    the user's email address
     * @param callback a {@link Callback} to handle success or failure of the operation
     */
    public void signUp(String name, String email, Callback<Void> callback) {
        FBInstallations.getCurrentFID(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fid = task.getResult();
                FBInstallations.setFid(fid);

                User newUser = new User();
                newUser.setId(fid);
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPhone(null);
                newUser.setNotifications(true);
                newUser.setAvatar(null);

                CurrentUser user = CurrentUser.getInstance();
                user.setFid(fid);
                user.setName(name);
                user.setEmail(email);
                user.setPhone(null);
                user.setNotifications(true);
                user.setAvatar(null);

                UserDB.getInstance().createUser(newUser, dbtask -> {
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

    /**
     * Checks if a user already exists in Firestore.
     * <p>
     * Retrieves the current Firebase Installation ID (FID) and queries Firestore to see if a user
     * document exists with that ID.
     *
     * @param callback a {@link Callback} that receives {@code true} if the user exists, {@code false} otherwise,
     *                 or an exception if the operation fails
     */
    public void userExists(Callback<Boolean> callback) {
        FBInstallations.getCurrentFID(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fid = task.getResult();
                FBInstallations.setFid(fid);
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

    /**
     * Validates a user's name.
     *
     * @param name the name to validate
     * @return an error message if invalid, or {@code null} if valid
     */
    public String validateName(String name) {
        if (name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    /**
     * Validates a user's email address.
     *
     * @param email the email to validate
     * @return an error message if invalid, or {@code null} if valid
     */
    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }
}
