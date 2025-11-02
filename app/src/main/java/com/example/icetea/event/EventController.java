package com.example.icetea.event;

import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventController {

    private EventDB eventDB;

    public EventController() {
        eventDB = EventDB.getInstance();
    }

    public void createEvent(Event event, Callback<Void> callback) {
        eventDB.saveEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error when creating event"));
            }
        });
    }

    public void getEventById(String eventId, Callback<Event> callback) {
        eventDB.getEvent(eventId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) event.setId(doc.getId());
                    callback.onSuccess(event);
                } else {
                    callback.onFailure(new Exception("Event not found"));
                }
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Failed to get event"));
            }
        });
    }
    public void getEventsByOrganizerId(String organizerId, Callback<List<Event>> callback) {
        eventDB.getEventsByOrganizerId(organizerId, task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null)  {
                        e.setId(doc.getId());
                        events.add(e);
                    }
                }
                callback.onSuccess(events);
            } else {
                callback.onFailure(new Exception("getEventsByOrganizerId : EventController failed"));
            }
        });
    }

    public void updateEvent(Event event, Callback<Void> callback) {
        eventDB.saveEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error when creating event"));
            }
        });
    }

    public void deleteEvent(Event event, Callback<Void> callback) {
        eventDB.deleteEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error deleting event")
                );
            }
        });
    }
}
