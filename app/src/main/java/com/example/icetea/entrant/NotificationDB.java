package com.example.icetea.entrant;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class NotificationDB {

    private static NotificationDB instance;
    private final CollectionReference notificationsCollection;

    private NotificationDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("Notification");
    }

    public static NotificationDB getInstance() {
        if (instance == null) {
            instance = new NotificationDB();
        }
        return instance;
    }

    public void getNotificationsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        notificationsCollection
                .whereEqualTo("userID", userId)  // ✅ Matches your Firestore field exactly
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getNotification(String notificationId, OnCompleteListener<DocumentSnapshot> listener) {
        notificationsCollection.document(notificationId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void saveNotification(Notification notification, OnCompleteListener<Void> listener) {
        String id = notification.getId();
        if (id == null || id.isEmpty()) {
            id = notificationsCollection.document().getId();
            notification.setId(id);
        }
        notificationsCollection.document(id)
                .set(notification)
                .addOnCompleteListener(listener);
    }

    public void deleteNotification(String id, OnCompleteListener<Void> listener) {
        notificationsCollection.document(id)
                .delete()
                .addOnCompleteListener(listener);
    }
}