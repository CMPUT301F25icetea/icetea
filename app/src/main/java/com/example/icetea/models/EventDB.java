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
 * Singleton class that provides access to the Firestore "events" collection.
 * <p>
 * Provides methods for creating, retrieving, updating, deleting, and querying
 * Event documents. Designed as a singleton to ensure only one instance
 * interacts with Firestore.
 * <p>
 * Firestore structure assumed:
 * <ul>
 *     <li>Collection: events</li>
 *     <li>Fields: eventId, organizerId, registrationEndDate, posterBase64, etc.</li>
 * </ul>
 */
public class EventDB {

    /** The singleton instance of EventDB */
    private static EventDB instance;

    /** Firestore collection reference for events */
    private final CollectionReference eventsCollection;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes Firestore collection reference.
     */
    private EventDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    /**
     * Retrieves the singleton instance of EventDB.
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
     * Creates a new event in Firestore.
     * <p>
     * If the provided {@link Event} has no ID, a new document ID is generated
     * and set on the Event object.
     *
     * @param event    The Event object to create in Firestore
     * @param listener Listener to handle completion of the operation
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
     * Retrieves a single event by its ID.
     *
     * @param eventId  The ID of the event to retrieve
     * @param listener Listener to handle the resulting DocumentSnapshot
     */
    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates fields of an existing event in Firestore.
     * <p>
     * Only the fields specified in the updates map will be modified.
     *
     * @param eventId  The ID of the event to update
     * @param updates  Map of field names to updated values
     * @param listener Listener to handle completion of the operation
     */
    public void updateEvent(String eventId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        eventsCollection.document(eventId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all events organized by a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @param listener    Listener to handle the resulting QuerySnapshot
     */
    public void getEventsByOrganizer(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection.whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes an event and its associated waitlist entries in Firestore.
     * <p>
     * This operation uses a Firestore batch to ensure that both the event and
     * waitlist documents are deleted atomically.
     *
     * @param eventId  The ID of the event to delete
     * @param listener Listener to handle completion of the operation
     *
     * <p><b>Note:</b> If no event with the given ID exists, this currently returns
     * a successful task with null result. You may want to handle this explicitly.
     */
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

    /**
     * Retrieves all events that are currently active.
     * <p>
     * Active events are defined as events whose registrationEndDate is
     * later than the current timestamp.
     *
     * @param listener Listener to handle the resulting QuerySnapshot
     */
    public void getActiveEvents(OnCompleteListener<QuerySnapshot> listener) {
        Timestamp now = Timestamp.now();
        eventsCollection
                .whereGreaterThan("registrationEndDate", now)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all events in the Firestore collection.
     *
     * @param listener Listener to handle the resulting QuerySnapshot
     */
    public void getAllEvents(OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all events that have a poster image (posterBase64 field not empty).
     *
     * @param listener Listener to handle the resulting QuerySnapshot
     */
    public void getAllEventsWithPoster(OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection
                .whereGreaterThan("posterBase64", "")
                .get()
                .addOnCompleteListener(listener);
    }
}