package com.example.icetea.models;

import com.google.firebase.Timestamp;
import java.util.List;

/**
 * Model class representing a notification item from the admin perspective.
 * <p>
 * Each notification item contains details about the event, the message,
 * the recipients, their statuses, and a timestamp.
 * </p>
 * <p>
 * Typically used for displaying notifications in an admin dashboard or logs.
 * </p>
 */
public class NotificationItemAdmin {

    /** The ID of the event this notification is associated with */
    private String eventId;

    /** The title of the notification */
    private String title;

    /** The message body of the notification */
    private String message;

    /** List of user IDs who received this notification */
    private List<String> recipients;

    /** List of statuses corresponding to recipients (e.g., delivered, read) */
    private List<String> statuses;

    /** The timestamp when this notification was created */
    private Timestamp timestamp;

    /**
     * Required empty constructor for Firestore deserialization or serialization frameworks
     */
    public NotificationItemAdmin() {
        // req
    }

    /**
     * Constructs a new {@link NotificationItemAdmin} with the given details.
     *
     * @param eventId    The event ID this notification is associated with
     * @param title      The title of the notification
     * @param message    The message content of the notification
     * @param recipients List of user IDs who received the notification
     * @param statuses   List of statuses corresponding to each recipient
     * @param timestamp  The creation timestamp of the notification
     */
    public NotificationItemAdmin(String eventId, String title, String message,
                                 List<String> recipients, List<String> statuses, Timestamp timestamp) {
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.recipients = recipients;
        this.statuses = statuses;
        this.timestamp = timestamp;
    }

    /** @return The event ID this notification is associated with */
    public String getEventId() {
        return eventId;
    }

    /** @param eventId The event ID this notification is associated with */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /** @return The title of the notification */
    public String getTitle() {
        return title;
    }

    /** @param title The title of the notification */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return The message content of the notification */
    public String getMessage() {
        return message;
    }

    /** @param message The message content of the notification */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return The list of recipient user IDs */
    public List<String> getRecipients() {
        return recipients;
    }

    /** @param recipients The list of recipient user IDs */
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    /** @return The list of statuses corresponding to recipients */
    public List<String> getStatuses() {
        return statuses;
    }

    /** @param statuses The list of statuses corresponding to recipients */
    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    /** @return The creation timestamp of the notification */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /** @param timestamp The creation timestamp of the notification */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}