package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * Singleton class that provides database access for User objects using Firebase Firestore.
 * <p>
 * Provides methods to create, read, update, and query User documents.
 * All operations are asynchronous and use {@link OnCompleteListener} callbacks to handle results.
 * </p>
 *
 * <p>Firestore Collection Path: /users/{userId}</p>
 */
public class UserDB {

    /** Singleton instance of UserDB */
    private static UserDB instance;

    /** Reference to the 'users' collection in Firestore */
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
     * Creates a new user in Firestore.
     *
     * @param user     The {@link User} object to save
     * @param listener {@link OnCompleteListener} called when the operation completes
     */
    public void createUser(User user, OnCompleteListener<Void> listener) {
        usersCollection.document(user.getId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a user document by user ID.
     *
     * @param fid      The user ID to fetch
     * @param listener {@link OnCompleteListener} called with the {@link DocumentSnapshot} result
     */
    public void getUser(String fid, OnCompleteListener<DocumentSnapshot> listener) {
        usersCollection.document(fid)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates fields of an existing user document.
     *
     * @param fid      The user ID to update
     * @param updates  {@link HashMap} of field names and values to update
     * @param listener {@link OnCompleteListener} called when the update completes
     */
    public void updateUser(String fid, HashMap<String, Object> updates, OnCompleteListener<Void> listener) {
        usersCollection.document(fid)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all user documents in the Firestore 'users' collection.
     *
     * @param listener {@link OnCompleteListener} called with the {@link QuerySnapshot} result
     */
    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all users who have a non-empty avatar field.
     *
     * @param listener {@link OnCompleteListener} called with the {@link QuerySnapshot} result
     */
    public void getAllUsersWithAvatar(OnCompleteListener<QuerySnapshot> listener) {
        usersCollection
                .whereGreaterThan("avatar", "")
                .get()
                .addOnCompleteListener(listener);
    }
}