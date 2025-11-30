package com.example.icetea.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class NotificationItemAdmin {
    private String eventId;
    private String title;
    private String message;
    private List<String> recipients;
    private List<String> statuses;
    private Timestamp timestamp;

    public NotificationItemAdmin() {
        // req
    }

    public NotificationItemAdmin(String eventId, String title, String message,
                                 List<String> recipients, List<String> statuses, Timestamp timestamp) {
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.recipients = recipients;
        this.statuses = statuses;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
