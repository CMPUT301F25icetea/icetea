package com.example.icetea.event;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    /**
     * Retrieves all events for a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @param listener Listener to handle the query snapshot
     */
    public void getEventsByOrganizerId(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection.whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes an Event from Firestore.
     *
     * @param event The Event to delete
     * @param listener Listener to handle completion
     */
    public void deleteEvent(Event event, OnCompleteListener<Void> listener) {
        eventsCollection.document(event.getEventId())
                .delete()
                .addOnCompleteListener(listener);
    }

}
