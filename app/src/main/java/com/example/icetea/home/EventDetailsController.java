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

/**
 * Controller class responsible for retrieving and updating event details
 * and waitlist status for users. Provides helper methods to convert
 * timestamps and communicate with Firestore database wrapper classes.
 */
public class EventDetailsController {

    /** Date formatter used for displaying Firebase timestamps. */
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy @ h:mm a", Locale.getDefault());

    /**
     * Converts a Firestore {@link Timestamp} into a formatted date string.
     *
     * @param timestamp the Firebase timestamp to convert (may be null)
     * @return a formatted string representation of the timestamp,
     *         or null if the timestamp is null
     */
    public String timestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return sdf.format(timestamp.toDate());
    }

    /**
     * Retrieves an {@link Event} object for the given event ID.
     *
     * @param eventId  the Firestore document ID of the event
     * @param callback callback returning the loaded {@link Event} on success,
     *                 or an error on failure
     */
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

    /**
     * Retrieves the waitlist entry status for a given user and event.
     *
     * @param userId   the user ID
     * @param eventId  the event ID
     * @param callback callback returning:
     *                 <ul>
     *                     <li>the status string ("pending", "accepted", etc.)</li>
     *                     <li>null if no entry exists or status is missing</li>
     *                     <li>a failure if the Firestore task errors</li>
     *                 </ul>
     */
    public void getEntrantStatus(String userId, String eventId, Callback<String> callback) {
        WaitlistDB.getInstance().getWaitlistEntry(userId, eventId, task -> {
            if (!task.isSuccessful()) {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error loading waitlist status"));
                return;
            }
            DocumentSnapshot doc = task.getResult();

            if (doc == null || !doc.exists()) {
                callback.onSuccess(null);
                return;
            }

            String status = doc.getString("status");
            if (status == null) {
                callback.onSuccess(null);
                return;
            }

            callback.onSuccess(status);
        });
    }

    /**
     * Updates a user's waitlist status for a specific event.
     *
     * @param userId    the user ID
     * @param eventId   the event ID
     * @param newStatus the new status value to write
     * @param callback  callback invoked on success or failure
     */
    public void updateEntrantStatus(String userId, String eventId, String newStatus, Callback<Void> callback) {
        WaitlistDB.getInstance().updateWaitlistStatus(userId, eventId, newStatus, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error updating status"));
            }
        });
    }

    /**
     * Removes a user from an event’s waitlist.
     *
     * @param userId   the user ID
     * @param eventId  the event ID
     * @param callback callback invoked on success or failure
     */
    public void removeFromWaitlist(String userId, String eventId, Callback<Void> callback) {
        WaitlistDB.getInstance().removeFromWaitlist(userId, eventId, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error deleting waitlist"));
            }
        });
    }

    /**
     * Adds a new waitlist entry for an event.
     *
     * @param waitlist the {@link Waitlist} object to add
     * @param callback callback invoked on success or failure
     */
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