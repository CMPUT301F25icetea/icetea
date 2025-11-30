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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing CRUD operations on Waitlist entries in Firestore.
 *
 * <p>This class handles adding, removing, updating, and retrieving waitlist entries.
 * It also updates the corresponding event's current entrants count when a user joins
 * or leaves the waitlist. All operations are performed atomically using Firestore
 * WriteBatch operations.</p>
 *
 * <p>Firestore collection paths:</p>
 * <ul>
 *     <li>Waitlist: /waitlist/{userId_eventId}</li>
 *     <li>Events: /events/{eventId}</li>
 * </ul>
 */
public class WaitlistDB {

    /** Singleton instance of WaitlistDB. */
    private static WaitlistDB instance;

    /** Reference to the 'waitlist' collection in Firestore. */
    private final CollectionReference waitlistCollection;

    /** Reference to the 'events' collection in Firestore. */
    private final CollectionReference eventsCollection;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes Firestore and collection references.
     */
    private WaitlistDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
        eventsCollection = db.collection("events");
    }

    /**
     * Returns the singleton instance of WaitlistDB.
     *
     * @return WaitlistDB instance.
     */
    public static WaitlistDB getInstance() {
        if (instance == null) {
            instance = new WaitlistDB();
        }
        return instance;
    }

    /**
     * Adds a user to the waitlist for an event and increments the event's current entrants count.
     *
     * @param waitlistObj The Waitlist object containing user and event details.
     * @param listener Callback to be invoked upon completion of the operation.
     */
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

    /**
     * Removes a user from a waitlist and decrements the event's current entrants count.
     *
     * @param userId User ID to remove.
     * @param eventId Event ID from which the user is removed.
     * @param listener Callback invoked upon completion.
     */
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

    /**
     * Retrieves a specific waitlist entry for a given user and event.
     *
     * @param userId User ID.
     * @param eventId Event ID.
     * @param listener Callback invoked with the DocumentSnapshot of the waitlist entry.
     */
    public void getWaitlistEntry(String userId, String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        waitlistCollection.document(userId + "_" + eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates the status of a waitlist entry.
     *
     * @param userId User ID.
     * @param eventId Event ID.
     * @param newStatus New status to set (e.g., "confirmed", "cancelled").
     * @param listener Callback invoked upon completion.
     */
    public void updateWaitlistStatus(String userId, String eventId, String newStatus, OnCompleteListener<Void> listener) {
        String waitlistId = userId + "_" + eventId;

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", newStatus);

        waitlistCollection.document(waitlistId)
                .update(updateData)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all waitlist entries for a specific event.
     *
     * @param eventId Event ID.
     * @param listener Callback invoked with the QuerySnapshot of matching entries.
     */
    public void getEventWaitlist(String eventId, OnCompleteListener<QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all waitlist entries for a specific user.
     *
     * @param userId User ID.
     * @param listener Callback invoked with the QuerySnapshot of matching entries.
     */
    public void getUserWaitlist(String userId, OnCompleteListener<QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all entrants for an event filtered by status.
     *
     * @param eventId Event ID.
     * @param status Status to filter by (e.g., "confirmed", "pending").
     * @param listener Callback invoked with the QuerySnapshot of matching entries.
     */
    public void getEntrantsByStatus(String eventId, String status, OnCompleteListener<QuerySnapshot> listener) {
        waitlistCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Listens for real-time updates to the waitlist of a specific event.
     *
     * @param eventId Event ID.
     * @param listener EventListener invoked when changes occur in the waitlist.
     * @return ListenerRegistration to remove the listener when no longer needed.
     */
    public ListenerRegistration listenToWaitlist(String eventId, EventListener<QuerySnapshot> listener) {
        return FirebaseFirestore.getInstance()
                .collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .addSnapshotListener(listener);
    }
}