package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationsLogDB {

    private static NotificationsLogDB instance;
    private final CollectionReference notificationsCollection;

    private NotificationsLogDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("notificationsLog");
    }

    public static NotificationsLogDB getInstance() {
        if (instance == null) instance = new NotificationsLogDB();
        return instance;
    }

    public void getAllNotifications(OnCompleteListener<QuerySnapshot> listener) {
        notificationsCollection.get().addOnCompleteListener(listener);
    }
}
