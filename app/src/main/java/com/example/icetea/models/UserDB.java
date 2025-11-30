package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    public void createUser(User user, OnCompleteListener<Void> listener) {
        usersCollection.document(user.getId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a User document by ID from Firestore.
     *
     * @param fid User ID
     * @param listener Listener to handle the retrieved document
     */
    public void getUser(String fid, OnCompleteListener<DocumentSnapshot> listener) {
        usersCollection.document(fid)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates fields of a User document in Firestore.
     *
     * @param fid User ID
     * @param updates HashMap of fields to update
     * @param listener Listener to handle completion
     */
    public void updateUser(String fid, HashMap<String, Object> updates, OnCompleteListener<Void> listener) {
        usersCollection.document(fid)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.get()
                .addOnCompleteListener(listener);
    }
    public void getAllUsersWithAvatar(OnCompleteListener<QuerySnapshot> listener) {
        usersCollection
                .whereGreaterThan("avatar", "")
                .get()
                .addOnCompleteListener(listener);
    }
}
