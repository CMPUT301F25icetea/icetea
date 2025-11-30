package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Singleton class that provides database access for notification logs using Firebase Firestore.
 * <p>
 * This class handles retrieval of all notifications stored in the 'notificationsLog' collection.
 * </p>
 *
 * <p>Firestore Collection Path: /notificationsLog/{autoId}</p>
 */
public class NotificationsLogDB {

    /** Static singleton instance */
    private static NotificationsLogDB instance;

    /** Reference to the 'notificationsLog' collection in Firestore */
    private final CollectionReference notificationsCollection;

    /**
     * Private constructor to enforce the singleton pattern.
     * Initializes the Firestore collection reference.
     */
    private NotificationsLogDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("notificationsLog");
    }

    /**
     * Returns the singleton instance of {@link NotificationsLogDB}.
     *
     * @return NotificationsLogDB instance
     */
    public static NotificationsLogDB getInstance() {
        if (instance == null) instance = new NotificationsLogDB();
        return instance;
    }

    /**
     * Retrieves all notification log entries from Firestore.
     *
     * @param listener {@link OnCompleteListener} to handle the completion of the query.
     *                 Provides a {@link QuerySnapshot} containing all notifications.
     */
    public void getAllNotifications(OnCompleteListener<QuerySnapshot> listener) {
        notificationsCollection.get().addOnCompleteListener(listener);
    }
}