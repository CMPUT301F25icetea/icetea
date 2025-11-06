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

public class EventDB {
    private final CollectionReference eventsCollection;
    private final CollectionReference waitlistCollection;
    private static EventDB instance;

    private EventDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
        waitlistCollection = db.collection("waitlist");
    }

    public static EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void joinWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        String waitlistId = eventId + "_" + userId;
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("eventId", eventId);
        waitlistEntry.put("userId", userId);
        waitlistEntry.put("joinedAt", System.currentTimeMillis());
        waitlistEntry.put("status", "waiting");

        waitlistCollection.document(waitlistId)
                .set(waitlistEntry)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Increment waitlist count in event
                        eventsCollection.document(eventId)
                                .update("waitlistCount", FieldValue.increment(1))
                                .addOnCompleteListener(listener);
                    } else {
                        listener.onComplete(task);
                    }
                });
    }

    public void isUserInWaitlist(String eventId, String userId, OnCompleteListener<DocumentSnapshot> listener) {
        String waitlistId = eventId + "_" + userId;
        waitlistCollection.document(waitlistId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void leaveWaitlist(String eventId, String userId, OnCompleteListener<Void> listener) {
        String waitlistId = eventId + "_" + userId;
        waitlistCollection.document(waitlistId)
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

    public void updateEvent(Event event, OnCompleteListener<Void> listener) {
        if (event.getId() == null || event.getId().isEmpty()) {
            listener.onComplete(null);
            return;
        }

        eventsCollection.document(event.getId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    public void deleteEvent(String eventId, OnCompleteListener<Void> listener) {
        eventsCollection.document(eventId)
                .delete()
                .addOnCompleteListener(listener);
    }
}

