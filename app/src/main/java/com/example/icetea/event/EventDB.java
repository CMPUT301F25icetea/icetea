package com.example.icetea.event;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class EventDB {

    private static EventDB instance;
    private final CollectionReference eventsCollection;

    private EventDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    public static EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    public void saveEvent(Event event, OnCompleteListener<Void> listener) {
        String id = event.getId();
        if (id == null) {
            id = eventsCollection.document().getId();
            event.setId(id);
        }
        eventsCollection.document(id)
                .set(event)
                .addOnCompleteListener(listener);
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getEventsByOrganizerId(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        eventsCollection.whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void deleteEvent(Event event, OnCompleteListener<Void> listener) {
        eventsCollection.document(event.getId())
                .delete()
                .addOnCompleteListener(listener);
    }

}