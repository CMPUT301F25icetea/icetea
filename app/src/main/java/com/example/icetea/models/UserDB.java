package com.example.icetea.models;

import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserDB {

    private static UserDB instance;
    private final CollectionReference usersCollection;

    private UserDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    public static UserDB getInstance() {
        if (instance == null) {
            instance = new UserDB();
        }
        return instance;
    }

    public void saveUser(User user, OnCompleteListener<Void> listener) {
        usersCollection.document(user.getId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    public void getUser(String id, OnCompleteListener<DocumentSnapshot> listener) {
        usersCollection.document(id)
                .get()
                .addOnCompleteListener(listener);
    }

    public void updateUser(String id, HashMap<String, Object> updates, OnCompleteListener<Void> listener) {
        usersCollection.document(id)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    public void deleteUser(String id, OnCompleteListener<Void> listener) {
        usersCollection.document(id)
                .delete()
                .addOnCompleteListener(listener);
    }
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
}
