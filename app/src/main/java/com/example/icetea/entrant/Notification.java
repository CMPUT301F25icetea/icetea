package com.example.icetea.entrant;

public class Notification {
    private String id;
    private String userId;
    private String eventId;
    private String eventName;
    private String type;  // "won", "replacement", "lost"
    private String message;
    private long timestamp;

    public Notification() {
        // Required for Firestore
    }

    public Notification(String id, String userId, String eventId, String eventName, String type, String message, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
