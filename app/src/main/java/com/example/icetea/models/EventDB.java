package com.example.icetea.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Map;

/**
 * Singleton class that provides database access for Event objects using Firebase Firestore.
 * Handles CRUD operations for events.
 */
public class EventDB {

    private static EventDB instance;
    private final CollectionReference eventsCollection;

    /**
     * Private constructor initializes the Firestore collection reference for events.
     */
    private EventDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    /**
     * Returns the singleton instance of EventDB.
     *
     * @return EventDB instance
     */
    public static EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    /**
     * Saves an Event object to Firestore. If the Event has no ID, a new document ID is generated.
     *
     * @param event The Event to save
     * @param listener Listener to handle completion
     */
    public void createEvent(Event event, OnCompleteListener<Void> listener) {
        String id = event.getEventId();
        if (id == null) {
            id = eventsCollection.document().getId();
            event.setEventId(id);
        }
        eventsCollection.document(id)
                .set(event)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves an Event by its ID from Firestore.
     *
     * @param eventId The ID of the Event to retrieve
     * @param listener Listener to handle the retrieved document
     */
    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void updateEvent(String eventId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        eventsCollection.document(eventId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all events for a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @param listener Listener to handle the query snapshot
     */
    public void getEventsByOrganizer(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection.whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void deleteEvent(String eventId, OnCompleteListener<Void> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsCol = db.collection("events");
        CollectionReference waitlistCol = db.collection("waitlist");

        Task<QuerySnapshot> eventQuery =
                eventsCol.whereEqualTo("eventId", eventId).get();

        Task<QuerySnapshot> waitlistQuery =
                waitlistCol.whereEqualTo("eventId", eventId).get();

        Tasks.whenAllSuccess(eventQuery, waitlistQuery)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onComplete(Tasks.forException(task.getException() != null ? task.getException() : new Exception("Error deleting event")));
                        return;
                    }

                    QuerySnapshot eventDocs = eventQuery.getResult();
                    QuerySnapshot waitlistDocs = waitlistQuery.getResult();

                    if (eventDocs.isEmpty()) {
                        listener.onComplete(Tasks.forResult(null));
                        return;
                    }

                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : eventDocs.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    for (DocumentSnapshot doc : waitlistDocs.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit().addOnCompleteListener(listener);
                });
    }


    public void getActiveEvents(OnCompleteListener<QuerySnapshot> listener) {
        Timestamp now = Timestamp.now();
        eventsCollection
                .whereGreaterThan("registrationEndDate", now)
                .get()
                .addOnCompleteListener(listener);
    }
    public void getAllEvents(OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection
                .get()
                .addOnCompleteListener(listener);
    }
    public void getAllEventsWithPoster(OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection
                .whereGreaterThan("posterBase64", "")
                .get()
                .addOnCompleteListener(listener);
    }

}
