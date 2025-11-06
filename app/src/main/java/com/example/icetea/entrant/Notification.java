package com.example.icetea.entrant;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String userID; // ✅ matches Firestore field exactly
    private String eventId;
    private String eventName;
    private String type;   // "won", "replacement", "lost"
    private String message;
    private Timestamp timestamp; // ✅ Firestore Timestamp object

    public Notification() {
        // Required for Firestore
    }

    public Notification(String id, String userID, String eventId, String eventName, String type, String message, Timestamp timestamp) {
        this.id = id;
        this.userID = userID;
        this.eventId = eventId;
        this.eventName = eventName;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    // Helper to get timestamp as long for adapter
    public long getTimestampMillis() {
        return timestamp != null ? timestamp.toDate().getTime() : 0;
    }
}
