package com.example.icetea.home;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailsController {

    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy @ h:mm a", Locale.getDefault());


    public String timestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return sdf.format(timestamp.toDate());
    }

    public void getEventObject(String eventId, Callback<Event> callback) {
        EventDB.getInstance().getEvent(eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error loading event"));
                return;
            }
            Event event = task.getResult().toObject(Event.class);
            if (event == null) {
                callback.onFailure(new Exception("Event is null"));
                return;
            }
            callback.onSuccess(event);
        });
    }

    public void getEntrantStatus(String userId, String eventId, Callback<String> callback) {
        WaitlistDB.getInstance().getWaitlistEntry(userId, eventId, task -> {
            if (!task.isSuccessful()) {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error loading waitlist status"));
                return;
            }
            DocumentSnapshot doc = task.getResult();

            // no entry
            if (doc == null || !doc.exists()) {
                callback.onSuccess(null);
                return;
            }

            // shouldn't ever happen
            String status = doc.getString("status");
            if (status == null) {
                callback.onSuccess(null);
                return;
            }
            callback.onSuccess(status);
        });
    }

    public void updateEntrantStatus(String userId, String eventId, String newStatus, Callback<Void> callback) {

        WaitlistDB.getInstance().updateWaitlistStatus(userId, eventId, newStatus, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error updating status"));
            }
        });
    }

    public void removeFromWaitlist(String userId, String eventId, Callback<Void> callback) {
        WaitlistDB.getInstance().removeFromWaitlist(userId, eventId, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error deleting waitlist"));
            }
        });
    }

    public void addToWaitlist(Waitlist waitlist, Callback<Void> callback) {

        WaitlistDB.getInstance().addToWaitlist(waitlist, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error adding to waitlist"));
            }
        });

    }

}
