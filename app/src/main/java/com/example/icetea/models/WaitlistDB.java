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
    /** Reference to the 'events' collection in Firestore (for updating counts). */
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

    // ==================== CRUD Operations ====================

    /**
     * Adds a user to the waitlist for an event.
     * This is a multi-step operation:
     * 1. Checks the event's 'capacity' against its 'waitlistCount'.
     * 2. If not full, creates a new document in the 'waitlist' collection.
     * 3. Increments the 'waitlistCount' field in the 'events' document.
     * The listener will receive a null Task if the waitlist is full or if the event doesn't exist.
     *
     * @param eventId  The ID of the event to join.
     * @param userId   The ID of the user joining.
     * @param listener Completion listener (notified upon success or failure of the *entire* operation).
     */
    public void addToWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        // Get event document
        eventsCollection.document(eventId).get()
                .addOnSuccessListener(eventSnap -> {
                    if (!eventSnap.exists()) {
                        listener.onComplete(null); // Event doesn't exist
                        return;
                    }

                    // Retrieve capacity and count. Default to 0 if null.
                    Long currentCount = eventSnap.getLong("waitlistCount");
                    Long capacity = eventSnap.getLong("capacity"); // Assumes 'capacity' field exists on Event

                    long count = (currentCount != null) ? currentCount : 0;
                    long cap = (capacity != null) ? capacity : 0;

                    if (cap > 0 && count >= cap) {
                        // Waiting list is full
                        System.out.println("Waiting list is full for event: " + eventId);
                        listener.onComplete(null); // Notify listener of "failure" (full)
                        return;
                    }

                    // Waitlist is not full, proceed to add user
                    String documentId = Waitlist.createDocumentId(eventId, userId);
                    Waitlist waitlist = new Waitlist(eventId, userId);

                    waitlistCollection.document(documentId)
                            .set(waitlist)
                            .addOnSuccessListener(aVoid -> {
                                // 3. Increment waitlist count in the event
                                eventsCollection.document(eventId)
                                        .update("waitlistCount", FieldValue.increment(1))
                                        .addOnSuccessListener(unused -> {
                                            System.out.println("User added to waitlist successfully!");
                                            listener.onComplete(null); // Notify success
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Failed to update waitlist count: " + e.getMessage());
                                            listener.onComplete(null); // Notify failure
                                        });
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Failed to add user to waitlist: " + e.getMessage());
                                listener.onComplete(null); // Notify failure
                            });
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error loading event: " + e.getMessage());
                    listener.onComplete(null); // Notify failure
                });
    }


    /**
     * Removes a user from the waitlist for an event.
     * This is a two-step operation:
     * 1. Deletes the document from the 'waitlist' collection.
     * 2. Decrements the 'waitlistCount' field in the 'events' document.
     *
     * @param eventId  The ID of the event to leave.
     * @param userId   The ID of the user to remove.
     * @param listener Completion listener.
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
     * Gets a single waitlist entry for a specific user and event.
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user.
     * @param listener Completion listener containing the {@link DocumentSnapshot}.
     */
    public void getWaitlistEntry(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        waitlistCollection.document(documentId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates the 'status' field of a user's waitlist entry.
     *
     * @param eventId   The ID of the event.
     * @param userId    The ID of the user.
     * @param newStatus The new status string (e.g., Waitlist.STATUS_SELECTED).
     * @param listener  Completion listener.
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
     * Checks if a user is already in the waitlist for a given event.
     * (Checks for the existence of the waitlist document).
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user.
     * @param listener Completion listener containing the {@link DocumentSnapshot}.
     * The caller should check {@code task.getResult().exists()}.
     */
    public void isUserInWaitlist(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String documentId = Waitlist.createDocumentId(eventId, userId);

        waitlistCollection.document(documentId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Gets all waitlist entries (as a QuerySnapshot) for a given event.
     *
     * @param eventId  The ID of the event.
     * @param listener Completion listener for the {@link com.google.firebase.firestore.QuerySnapshot}.
     */
    public void getWaitlistForEvent(String eventId, @NonNull OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Gets all waitlist entries (as a QuerySnapshot) for a given user across all events.
     *
     * @param userId   The ID of the user.
     * @param listener Completion listener for the {@link com.google.firebase.firestore.QuerySnapshot}.
     */
    public void getWaitlistForUser(String userId, @NonNull OnCompleteListener<com.google.firebase.firestore.QuerySnapshot> listener) {
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

    // ==================== Helper ====================

    /**
     * Trims the waitlist if a new, smaller capacity is set by the organizer.
     * It removes users who joined most recently (descending 'joinedAt' order)
     * until the waitlist size matches the new limit.
     * This operation is asynchronous and does not have a completion listener.
     *
     * @param eventId  The ID of the event to trim.
     * @param newLimit The new maximum number of participants for the waitlist.
     */
    public void trimWaitlist(String eventId, int newLimit){
        waitlistCollection.whereEqualTo("eventId", eventId)
                .orderBy("joinedAt", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.size() > newLimit) {
                        int overLimit = snapshot.size() - newLimit;
                        for (int i = 0; i < overLimit; i++) {
                            DocumentSnapshot doc = snapshot.getDocuments().get(i);
                            doc.getReference().delete(); // Delete the most recent entries
                        }
                        // Set the waitlist count to the new limit
                        eventsCollection.document(eventId)
                                .update("waitlistCount", newLimit);
                    }
                });
    }

    /**
     * Helper method to mark a user's status as {@link Waitlist#STATUS_SELECTED}.
     * This does *not* update the event's waitlist count.
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user.
     * @param listener Completion listener.
     */
    public void markUserAsSelected(String eventId, String userId, OnCompleteListener<Void> listener) {
        updateWaitlistStatus(eventId, userId, Waitlist.STATUS_SELECTED, listener);
    }

    /**
     * Helper method to mark a user's status as {@link Waitlist#STATUS_CANCELLED}.
     * This *also* decrements the event's 'waitlistCount'.
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user.
     * @param listener Completion listener.
     */
    public void markUserAsCancelled(String eventId, String userId, OnCompleteListener<Void> listener) {
        updateWaitlistStatus(eventId, userId, Waitlist.STATUS_CANCELLED, task -> {
            if (task.isSuccessful()) {
                // If status update is successful, decrement the count
                eventsCollection.document(eventId)
                        .update("waitlistCount", FieldValue.increment(-1))
                        .addOnCompleteListener(listener);
            } else {
                listener.onComplete(task);
            }
        });
    }
}