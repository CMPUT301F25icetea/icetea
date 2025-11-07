package com.example.icetea.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * A Singleton class that acts as a repository (DAO) for managing Event data in Firestore.
 * It handles all database operations for the 'events' and 'waitlist' collections.
 */
public class EventDB {
    /** Reference to the 'events' collection in Firestore. */
    private final CollectionReference eventsCollection;
    /** Reference to the 'waitlist' collection in Firestore. */
    private final CollectionReference waitlistCollection;
    /** The static Singleton instance of EventDB. */
    private static EventDB instance;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the Firestore instance and collection references.
     */
    private EventDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
        waitlistCollection = db.collection("waitlist");
    }

    /**
     * Gets the singleton instance of the EventDB.
     *
     * @return The static {@link EventDB} instance.
     */
    public static EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    /**
     * Fetches a single event document from Firestore.
     *
     * @param eventId  The ID of the event to retrieve.
     * @param listener The listener to be called upon completion, containing the {@link DocumentSnapshot}.
     */
    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Adds a user to the waitlist for a specific event.
     * This is a two-step operation:
     * 1. Creates a new document in the 'waitlist' collection.
     * 2. Increments the 'waitlistCount' field in the 'events' document.
     *
     * @param eventId  The ID of the event to join.
     * @param userId   The ID of the user joining the waitlist.
     * @param listener The listener to be called after *both* operations are complete (or if the first fails).
     */
    public void joinWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        // Create a composite ID for the waitlist entry
        String waitlistId = eventId + "_" + userId;
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("eventId", eventId);
        waitlistEntry.put("userId", userId);
        waitlistEntry.put("joinedAt", System.currentTimeMillis());
        waitlistEntry.put("status", "waiting");

        // 1. Create the waitlist entry
        waitlistCollection.document(waitlistId)
                .set(waitlistEntry)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 2. Increment waitlist count in the event
                        eventsCollection.document(eventId)
                                .update("waitlistCount", FieldValue.increment(1))
                                .addOnCompleteListener(listener); // Pass the original listener
                    } else {
                        // If creating the waitlist entry fails, call the listener with the failure
                        listener.onComplete(task);
                    }
                });
    }

    /**
     * Checks if a specific user is already on the waitlist for an event.
     * It does this by checking for the existence of the composite waitlist document.
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user to check.
     * @param listener The listener to be called upon completion. The caller should
     * check {@code task.getResult().exists()} to determine the status.
     */
    public void isUserInWaitlist(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String waitlistId = eventId + "_" + userId;
        waitlistCollection.document(waitlistId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Removes a user from the waitlist for a specific event.
     * This is a two-step operation:
     * 1. Deletes the document from the 'waitlist' collection.
     * 2. Decrements the 'waitlistCount' field in the 'events' document.
     *
     * @param eventId  The ID of the event to leave.
     * @param userId   The ID of the user leaving the waitlist.
     * @param listener The listener to be called after *both* operations are complete (or if the first fails).
     */
    public void leaveWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        String waitlistId = eventId + "_" + userId;

        // 1. Delete the waitlist entry
        waitlistCollection.document(waitlistId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 2. Decrement waitlist count in the event
                        eventsCollection.document(eventId)
                                .update("waitlistCount", FieldValue.increment(-1))
                                .addOnCompleteListener(listener); // Pass the original listener
                    } else {
                        // If deleting the waitlist entry fails, call the listener with the failure
                        listener.onComplete(task);
                    }
                });
    }

    /**
     * Creates a new event document in Firestore.
     * If the provided {@link Event} object does not have an ID, a new one
     * will be generated and set on the object before creation.
     *
     * @param event    The {@link Event} object to create.
     * @param listener The listener to be called upon completion.
     */
    public void createEvent(Event event, OnCompleteListener<Void> listener) {
        if (event.getId() == null || event.getId().isEmpty()) {
            // Generate a new ID if not provided
            String newId = eventsCollection.document().getId();
            event.setId(newId);
        }

        eventsCollection.document(event.getId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    /**
     * Updates an existing event document in Firestore.
     * This method uses {@code set()}, so it will overwrite the entire document.
     * If the document doesn't exist, it will be created.
     *
     * @param event    The {@link Event} object with updated data. The ID must not be null.
     * @param listener The listener to be called upon completion.
     */
    public void updateEvent(Event event, OnCompleteListener<Void> listener) {
        if (event.getId() == null || event.getId().isEmpty()) {
            // Fail fast if ID is missing, as we can't update
            listener.onComplete(null); // Or manually create a failed task
            return;
        }

        eventsCollection.document(event.getId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes an event document from Firestore.
     * Note: This does not automatically clean up associated waitlist entries.
     *
     * @param eventId  The ID of the event to delete.
     * @param listener The listener to be called upon completion.
     */
    public void deleteEvent(String eventId, OnCompleteListener<Void> listener) {
        eventsCollection.document(eventId)
                .delete()
                .addOnCompleteListener(listener);
    }
}