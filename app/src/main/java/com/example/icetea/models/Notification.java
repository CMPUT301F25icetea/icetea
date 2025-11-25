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
    private String id;
    private String userId; // Changed from userID to match Firestore
    private String eventId;
    private String eventName;
    private String type;   // "won", "replacement", "lost"
    private String message;
    private Timestamp timestamp;
    private String status; // "pending", "accepted", "declined"

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Notification() {
        this.status = "pending"; // Default status
    }

    /**
     * Constructs a Notification with all fields.
     *
     * @param id the unique identifier for this notification
     * @param userId the ID of the user who receives this notification
     * @param eventId the ID of the related event
     * @param eventName the name of the related event
     * @param type the type of notification ("won", "replacement", or "lost")
     * @param message the notification message content
     * @param timestamp the Firestore timestamp when this notification was created
     */
    public Notification(String id, String userId, String eventId, String eventName, String type, String message, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.status = "pending";
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

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Gets the timestamp as milliseconds since epoch for adapter usage.
     *
     * @return the timestamp in milliseconds since epoch, or 0 if timestamp is null
     */
    public long getTimestampMillis() {
        return timestamp != null ? timestamp.toDate().getTime() : 0;
    }

    // Helper methods
    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isAccepted() {
        return "accepted".equals(status);
    }

    public boolean isDeclined() {
        return "declined".equals(status);
    }

    public boolean isWonNotification() {
        return "won".equalsIgnoreCase(type);
    }

    public boolean canRespond() {
        return isWonNotification() && isPending();
    }
}