package com.example.icetea.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Waitlist model representing a user's waitlist entry in Firestore.
 * Firestore Collection Path: /waitlist/{eventId}_{userId}
 *
 * Database Schema:
 * - eventId: String (reference to event)
 * - userId: String (user who joined)
 * - joinedAt: long (timestamp in milliseconds)
 * - status: String (waiting/selected/cancelled)
 */
public class Waitlist {
    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_SELECTED = "selected";
    public static final String STATUS_CANCELLED = "cancelled";

    private String eventId;
    private String userId;
    private long joinedAt;
    private String status;

    /**
     * Required empty constructor for Firestore deserialization
     */
    public Waitlist() {
        // Firestore requires this
    }

    /**
     * Constructor for creating a new waitlist entry
     *
     * @param eventId Event ID
     * @param userId User ID
     */
    public Waitlist(String eventId, String userId) {
        this.eventId = eventId;
        this.userId = userId;
        this.joinedAt = System.currentTimeMillis();
        this.status = STATUS_WAITING;
    }

    /**
     * Full constructor with all parameters
     *
     * @param eventId Event ID
     * @param userId User ID
     * @param joinedAt Timestamp when joined
     * @param status Current status
     */
    public Waitlist(String eventId, String userId, long joinedAt, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.status = status;
    }

    // ==================== Getters and Setters ====================

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ==================== Helper Methods ====================

    /**
     * Generate the Firestore document ID for this waitlist entry
     * Format: {eventId}_{userId}
     *
     * @return Document ID string
     */
    public String getDocumentId() {
        return eventId + "_" + userId;
    }

    /**
     * Create document ID from event and user IDs
     *
     * @param eventId Event ID
     * @param userId User ID
     * @return Document ID string
     */
    public static String createDocumentId(String eventId, String userId) {
        return eventId + "_" + userId;
    }

    /**
     * Get formatted join date
     * @return Formatted date string (e.g., "Nov 02, 2024")
     */
    public String getFormattedJoinedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(joinedAt));
    }

    /**
     * Get formatted join date and time
     * @return Formatted date-time string (e.g., "Nov 02, 2024 3:30 PM")
     */
    public String getFormattedJoinedAtWithTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        return sdf.format(new Date(joinedAt));
    }

    /**
     * Get time since joined in a human-readable format
     * @return Relative time string (e.g., "2 hours ago", "3 days ago")
     */
    public String getTimeSinceJoined() {
        long now = System.currentTimeMillis();
        long diff = now - joinedAt;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if (weeks > 0) {
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return "Just now";
        }
    }

    /**
     * Check if user is waiting
     * @return true if status is "waiting"
     */
    public boolean isWaiting() {
        return STATUS_WAITING.equals(status);
    }

    /**
     * Check if user was selected
     * @return true if status is "selected"
     */
    public boolean isSelected() {
        return STATUS_SELECTED.equals(status);
    }

    /**
     * Check if user cancelled
     * @return true if status is "cancelled"
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    /**
     * Mark this entry as selected
     */
    public void markAsSelected() {
        this.status = STATUS_SELECTED;
    }

    /**
     * Mark this entry as cancelled
     */
    public void markAsCancelled() {
        this.status = STATUS_CANCELLED;
    }

    /**
     * Validate waitlist data
     * @return Error message if invalid, null if valid
     */
    public String validate() {
        if (eventId == null || eventId.trim().isEmpty()) {
            return "Event ID is required";
        }
        if (userId == null || userId.trim().isEmpty()) {
            return "User ID is required";
        }
        if (status == null || status.trim().isEmpty()) {
            return "Status is required";
        }
        if (!STATUS_WAITING.equals(status) &&
                !STATUS_SELECTED.equals(status) &&
                !STATUS_CANCELLED.equals(status)) {
            return "Invalid status value";
        }
        return null; // Valid
    }

    // ==================== Object Methods ====================

    @Override
    public String toString() {
        return "Waitlist{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", joinedAt=" + getFormattedJoinedAt() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waitlist waitlist = (Waitlist) o;
        return getDocumentId().equals(waitlist.getDocumentId());
    }

    @Override
    public int hashCode() {
        return getDocumentId().hashCode();
    }
}