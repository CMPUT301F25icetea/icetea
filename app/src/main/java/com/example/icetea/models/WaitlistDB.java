package com.example.icetea.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * A Singleton class that handles CRUD operations for Waitlist entries in Firestore.
 * It manages the 'waitlist' collection and updates the 'events' collection
 * for waitlist counts.
 * Firestore Collection Path: /waitlist/{eventId_userId}
 */
public class WaitlistDB {
    /** The static Singleton instance of WaitlistDB. */
    private static WaitlistDB instance;
    /** Reference to the 'waitlist' collection in Firestore. */
    private final CollectionReference waitlistCollection;
    private final CollectionReference eventsCollection;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the Firestore instance and collection references.
     */
    private WaitlistDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
        eventsCollection = db.collection("events");
    }

    /**
     * Gets the singleton instance of the WaitlistDB.
     *
     * @return The static {@link WaitlistDB} instance.
     */
    public static WaitlistDB getInstance() {
        if (instance == null) {
            instance = new WaitlistDB();
        }
        return instance;
    }

    public void addToWaitlist(Waitlist waitlistObj, OnCompleteListener<Void> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        DocumentReference waitlistRef = waitlistCollection.document(waitlistObj.getId());
        batch.set(waitlistRef, waitlistObj);

        DocumentReference eventRef = eventsCollection.document(waitlistObj.getEventId());
        batch.update(eventRef, "currentEntrants", FieldValue.increment(1));

        batch.commit()
                .addOnCompleteListener(listener);
    }

    public void removeFromWaitlist(String userId, String eventId, OnCompleteListener<Void> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        String waitlistId = userId + "_" + eventId;

        DocumentReference waitlistRef = waitlistCollection.document(waitlistId);
        batch.delete(waitlistRef);

        DocumentReference eventRef = db.collection("events").document(eventId);
        batch.update(eventRef, "currentEntrants", FieldValue.increment(-1));

        batch.commit().addOnCompleteListener(listener);
    }


    public void getWaitlistEntry(String userId, String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        waitlistCollection.document(userId + "_" + eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void updateWaitlistStatus(String userId, String eventId, String newStatus, OnCompleteListener<Void> listener) {
        String waitlistId = userId + "_" + eventId;

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", newStatus);

        waitlistCollection.document(waitlistId)
                .update(updateData)
                .addOnCompleteListener(listener);
    }

    public void getEventWaitlist(String eventId, OnCompleteListener<QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }


    public void getUserWaitlist(String userId, OnCompleteListener<QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getEntrantsByStatus(String eventId, String status, OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(listener);
    }
    public ListenerRegistration listenToWaitlist(String eventId, EventListener<QuerySnapshot> listener) {
        return FirebaseFirestore.getInstance()
                .collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .addSnapshotListener(listener);
    }

}