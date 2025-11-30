package com.example.icetea.models;

import com.google.firebase.Timestamp;

/**
 * Represents a notification entity stored in Firestore.
 * <p>
 * Each notification is associated with a user and an event, and contains
 * a title, message, and timestamp. Notifications are typically sent
 * to inform users about their waitlist status, event updates, or other
 * relevant actions in the application.
 * </p>
 *
 * <p>Firestore Collection: /notifications</p>
 */
public class Notification {

    /** ID of the user who will receive the notification */
    private String userId;

    /** ID of the event related to this notification */
    private String eventId;

    /** The message content of the notification */
    private String message;

    /** The title of the notification */
    private String title;

    /** Timestamp indicating when the notification was created */
    private Timestamp timestamp;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Notification() {
        // Required empty constructor
    }

    /**
     * Constructs a new Notification instance with all properties set.
     *
     * @param userId    the recipient user ID
     * @param eventId   the related event ID
     * @param title     the title of the notification
     * @param message   the message body of the notification
     * @param timestamp the timestamp when the notification was created
     */
    public Notification(String userId, String eventId, String title, String message, Timestamp timestamp) {
        this.userId = userId;
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    /** @return the recipient user ID */
    public String getUserId() { return userId; }

    /** @param userId the recipient user ID */
    public void setUserId(String userId) { this.userId = userId; }

    /** @return the related event ID */
    public String getEventId() { return eventId; }

    /** @param eventId the related event ID */
    public void setEventId(String eventId) { this.eventId = eventId; }

    /** @return the message content of the notification */
    public String getMessage() { return message; }

    /** @param message the message content of the notification */
    public void setMessage(String message) { this.message = message; }

    /** @return the timestamp when the notification was created */
    public Timestamp getTimestamp() { return timestamp; }

    /** @param timestamp the timestamp when the notification was created */
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    /** @return the title of the notification */
    public String getTitle() { return title; }

    /** @param title the title of the notification */
    public void setTitle(String title) { this.title = title; }
}