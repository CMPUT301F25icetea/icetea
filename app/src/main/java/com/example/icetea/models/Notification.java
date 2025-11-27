package com.example.icetea.models;

import com.google.firebase.Timestamp;

/**
 * Represents a notification entity that is stored in Firestore.
 *
 * This class models a notification that is sent to users when they win, lose,
 * or are selected as a replacement in an event. Each notification contains
 * information about the event, the type of notification, and the timestamp
 * when it was created.
 *
 * @author IceTea
 * @version 1.0
 */
public class Notification {
    private String userId;
    private String eventId;
    private String message;
    private String title;
    private Timestamp timestamp;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Notification() {
        // req
    }

    public Notification(String userId, String eventId, String title, String message, Timestamp timestamp) {
        this.userId = userId;
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}