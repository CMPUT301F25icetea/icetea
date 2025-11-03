package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
