package com.example.icetea.models;

import android.telecom.Call;

import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Singleton class that provides database access for User objects using Firebase Firestore.
 * Handles CRUD operations for users and provides helper methods to retrieve user-specific data.
 */
public class UserDB {

    private static UserDB instance;
    private final CollectionReference usersCollection;

    /**
     * Private constructor initializes the Firestore collection reference for users.
     */
    private UserDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    /**
     * Returns the singleton instance of UserDB.
     *
     * @return UserDB instance
     */
    public static UserDB getInstance() {
        if (instance == null) {
            instance = new UserDB();
        }
        return instance;
    }

    /**
     * Saves a User object to Firestore.
     *
     * @param user The User to save
     * @param listener Listener to handle completion
     */
    public void saveUser(User user, OnCompleteListener<Void> listener) {
        usersCollection.document(user.getId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a User document by ID from Firestore.
     *
     * @param id User ID
     * @param listener Listener to handle the retrieved document
     */
    public void getUser(String id, OnCompleteListener<DocumentSnapshot> listener) {
        usersCollection.document(id)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates fields of a User document in Firestore.
     *
     * @param id User ID
     * @param updates HashMap of fields to update
     * @param listener Listener to handle completion
     */
    public void updateUser(String id, HashMap<String, Object> updates, OnCompleteListener<Void> listener) {
        usersCollection.document(id)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes a User document from Firestore.
     *
     * @param id User ID
     * @param listener Listener to handle completion
     */
    public void deleteUser(String id, OnCompleteListener<Void> listener) {
        usersCollection.document(id)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves the top role of a user.
     *
     * @param id User ID
     * @param callback Callback to return the role or error
     */
    public void getUserTopRole(String id, Callback<String> callback) {
        getUser(id, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String role = document.getString("role");
                    callback.onSuccess(role);
                } else {
                    callback.onFailure(new Exception("User not found"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Retrieves the email of a user.
     *
     * @param id User ID
     * @param callback Callback to return the email or error
     */
    public void getUserEmail(String id, Callback<String> callback) {
        getUser(id, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String email = document.getString("email");
                    callback.onSuccess(email);
                } else {
                    callback.onFailure(new Exception("User not found"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
