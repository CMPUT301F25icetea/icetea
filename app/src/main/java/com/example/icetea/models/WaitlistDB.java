package com.example.icetea.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * WaitlistDB handles CRUD operations for Waitlist entries in Firestore.
 * Firestore Collection Path: /waitlist/{eventId_userId}
 */
public class WaitlistDB {
    private static WaitlistDB instance;
    private final CollectionReference waitlistCollection;
    private final CollectionReference eventsCollection;

    private WaitlistDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
        eventsCollection = db.collection("events");
    }

    public static WaitlistDB getInstance() {
        if (instance == null) {
            instance = new WaitlistDB();
        }
        return instance;
    }

    // ==================== CRUD Operations ====================

    /**
     * Add user to waitlist for an event, check if waitlist capacity is full
     *
     * @param eventId  Event ID
     * @param userId   User ID
     * @param listener Completion listener
     */
    public void addToWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        // Get event document
        eventsCollection.document(eventId).get()
                .addOnSuccessListener(eventSnap -> {
                    if (!eventSnap.exists()) {
                        listener.onComplete(null);
                        return;
                    }

                    Long currentCount = eventSnap.getLong("waitlistCount");
                    Long capacity = eventSnap.getLong("capacity");

                    long count = (currentCount != null) ? currentCount : 0;
                    long cap = (capacity != null) ? capacity : 0;

                    if (cap > 0 && count >= cap) {
                        // Waiting list is full
                        System.out.println("Waiting list is full for event: " + eventId);
                        listener.onComplete(null);
                        return;
                    }

                    String documentId = Waitlist.createDocumentId(eventId, userId);
                    Waitlist waitlist = new Waitlist(eventId, userId);

                    waitlistCollection.document(documentId)
                            .set(waitlist)
                            .addOnSuccessListener(aVoid -> {
                                eventsCollection.document(eventId)
                                        .update("waitlistCount", FieldValue.increment(1))
                                        .addOnSuccessListener(unused -> {
                                            System.out.println("User added to waitlist successfully!");
                                            listener.onComplete(null);
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Failed to update waitlist count: " + e.getMessage());
                                            listener.onComplete(null);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Failed to add user to waitlist: " + e.getMessage());
                                listener.onComplete(null);
                            });
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error loading event: " + e.getMessage());
                    listener.onComplete(null);
                });
    }


    /**
     * Remove a user from the waitlist
     *
     * @param eventId  Event ID
     * @param userId   User ID
     * @param listener Completion listener
     */
    public void removeFromWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        waitlistCollection.document(documentId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Decrement waitlist count in event
                        eventsCollection.document(eventId)
                                .update("waitlistCount", FieldValue.increment(-1))
                                .addOnCompleteListener(listener);
                    } else {
                        listener.onComplete(task);
                    }
                });
    }


    /**
     * Get waitlist entry for a specific user-event pair
     *
     * @param eventId  Event ID
     * @param userId   User ID
     * @param listener Completion listener with DocumentSnapshot
     */
    public void getWaitlistEntry(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        waitlistCollection.document(documentId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Update status of a user's waitlist entry
     *
     * @param eventId  Event ID
     * @param userId   User ID
     * @param newStatus New status (waiting / selected / cancelled)
     * @param listener Completion listener
     */
    public void updateWaitlistStatus(String eventId, String userId, String newStatus, OnCompleteListener<Void> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", newStatus);

        waitlistCollection.document(documentId)
                .update(updateData)
                .addOnCompleteListener(listener);
    }

    /**
     * Check if a user is already in the waitlist for a given event
     *
     * @param eventId  Event ID
     * @param userId   User ID
     * @param listener Completion listener
     */
    public void isUserInWaitlist(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        waitlistCollection.document(documentId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Get all waitlist entries for a given event
     *
     * @param eventId  Event ID
     * @param listener Completion listener for query
     */
    public void getWaitlistForEvent(String eventId, @NonNull OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Get all waitlist entries for a given user
     *
     * @param userId  User ID
     * @param listener Completion listener for query
     */
    public void getWaitlistForUser(String userId, @NonNull OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Get the final list of entrants who enrolled (accepted) for the event
     *
     * @param eventId Event ID
     * @param listener Completion listener for query results
     */
    public void getFinalEntrants(String eventId, OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", Waitlist.STATUS_ACCEPTED)
                .get()
                .addOnCompleteListener(listener);
    }

    // ==================== Helper ====================
    /**
     *  When the organizer set the capacity limit after the limit has been reached, remove entrants
     *  based on most recent registration date
     */
    public void trimWaitlist(String eventId, int newLimit){
        waitlistCollection.whereEqualTo("eventId", eventId)
                .orderBy("joinedAt", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.size() > newLimit) {
                        int overLimit = snapshot.size() - newLimit;
                        for (int i = 0; i < overLimit; i++) {
                            DocumentSnapshot doc = snapshot.getDocuments().get(i);
                            doc.getReference().delete();
                        }
                        eventsCollection.document(eventId)
                                .update("waitlistCount", newLimit);
                    }
                });
    }

    /**
     * Marks a user as selected in the waitlist and optionally updates event stats
     */
    public void markUserAsSelected(String eventId, String userId, OnCompleteListener<Void> listener) {
        updateWaitlistStatus(eventId, userId, Waitlist.STATUS_SELECTED, listener);
    }

    /**
     * Marks a user as cancelled in the waitlist and decrements event count
     */
    public void markUserAsCancelled(String eventId, String userId, OnCompleteListener<Void> listener) {
        updateWaitlistStatus(eventId, userId, Waitlist.STATUS_CANCELLED, task -> {
            if (task.isSuccessful()) {
                eventsCollection.document(eventId)
                        .update("waitlistCount", FieldValue.increment(-1))
                        .addOnCompleteListener(listener);
            } else {
                listener.onComplete(task);
            }
        });
    }
}
