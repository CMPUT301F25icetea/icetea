package com.example.icetea.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the Firestore instance and collection references.
     */
    private WaitlistDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
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
        // Get event document
        waitlistCollection.document(waitlistObj.getId())
                .set(waitlistObj)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        listener.onComplete(task);
                        return;
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("currentEntrants", FieldValue.increment(1));
                    EventDB.getInstance().updateEvent(waitlistObj.getEventId(), updates, listener);
                });
    }

    public void removeFromWaitlist(String userId, String eventId, OnCompleteListener<Void> listener) {
        String waitlistId = userId + "_" + eventId;
        waitlistCollection.document(waitlistId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        listener.onComplete(task);
                        return;
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("currentEntrants", FieldValue.increment(-1));
                    EventDB.getInstance().updateEvent(eventId, updates, listener);

                });
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

    /**
     * Gets the list of entrants who have accepted their spot in the event.
     * Queries for entries where status is {@link Waitlist#STATUS_ACCEPTED}.
     *
     * @param eventId  The ID of the event.
     * @param listener Completion listener for the {@link com.google.firebase.firestore.QuerySnapshot}.
     */
    public void getFinalEntrants(String eventId, OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", Waitlist.STATUS_ACCEPTED)
                .get()
                .addOnCompleteListener(listener);
    }

}