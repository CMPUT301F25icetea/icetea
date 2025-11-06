package com.example.icetea.entrant;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles all Firestore operations for Notifications
 */
public class NotificationDB {
    private static NotificationDB instance;
    private final CollectionReference notificationCollection;

    private NotificationDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // ✅ Make sure this matches your Firestore collection name exactly
        notificationCollection = db.collection("Notification");
    }

    public static NotificationDB getInstance() {
        if (instance == null) {
            instance = new NotificationDB();
        }
        return instance;
    }

    /**
     * Gets notifications for a specific user, ordered by timestamp descending.
     */
    public void getNotificationsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        notificationCollection
                // ✅ Match field name in Firestore: "userID"
                .whereEqualTo("userId", userId)
                // Order by timestamp (latest first)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Adds a new notification document to Firestore.
     */
    public void addNotification(Notification notification, OnCompleteListener<Void> listener) {
        notificationCollection.document()
                .set(notification)
                .addOnCompleteListener(listener);
    }
}
