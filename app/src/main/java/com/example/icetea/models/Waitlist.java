package com.example.icetea.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Waitlist model representing a user's waitlist entry in Firestore.
 * This class maps to documents in the Firestore 'waitlist' collection.
 * Firestore Collection Path: /waitlist/{eventId}_{userId}
 *
 * Database Schema:
 * - eventId: String (reference to event)
 * - userId: String (user who joined)
 * - joinedAt: long (timestamp in milliseconds)
 * - status: String (waiting/selected/cancelled)
 */
public class Waitlist {
    // ==================== Constants ====================

    /** The user is currently on the waitlist. */
    public static final String STATUS_WAITING = "waiting";
    /** The user has been selected from the waitlist by the organizer. */
    public static final String STATUS_SELECTED = "selected";
    /** The user has voluntarily left the waitlist or event. */
    public static final String STATUS_CANCELLED = "cancelled";
    /** The user was selected and has accepted the spot. */
    public static final String STATUS_ACCEPTED = "accepted";
    /** The user was selected and has declined the spot. */
    public static final String STATUS_DECLINED = "declined";

    // ==================== Fields ====================

    /** The ID of the event this waitlist entry belongs to. */
    private String eventId;
    /** The ID of the user on the waitlist. */
    private String userId;
    /** The timestamp (in milliseconds) when the user joined the waitlist. */
    private long joinedAt;
    /** The current status of the waitlist entry (e.g., "waiting", "selected"). */
    private String status;

    // ==================== Constructors ====================

    /**
     * Required empty constructor for Firestore deserialization
     */
    public Waitlist() {
        // Firestore requires this
    }

    /**
     * Constructor for creating a new waitlist entry.
     * Automatically sets the joinedAt timestamp to now and status to "waiting".
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     */
    public Waitlist(String eventId, String userId) {
        this.eventId = eventId;
        this.userId = userId;
        this.joinedAt = System.currentTimeMillis();
        this.status = STATUS_WAITING;
    }

    /**
     * Full constructor with all parameters.
     *
     * @param eventId  The ID of the event.
     * @param userId   The ID of the user.
     * @param joinedAt The timestamp when the user joined.
     * @param status   The current status of the entry.
     */
    public Waitlist(String eventId, String userId, long joinedAt, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.status = status;
    }

    // ==================== Getters and Setters ====================

    /**
     * @return The ID of the event.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @param eventId The ID of the event.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * @return The ID of the user.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The ID of the user.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The join timestamp (in milliseconds).
     */
    public long getJoinedAt() {
        return joinedAt;
    }

    /**
     * @param joinedAt The join timestamp (in milliseconds).
     */
    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     * @return The current status string (e.g., "waiting").
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The current status string (e.g., "waiting").
     */
    public void setStatus(String status) {
        this.status = status;
    }

    // ==================== Helper Methods ====================

    /**
     * Generate the Firestore document ID for this waitlist entry.
     * Format: {eventId}_{userId}
     *
     * @return A composite Document ID string.
     */
    public String getDocumentId() {
        return eventId + "_" + userId;
    }

    /**
     * Create document ID from event and user IDs.
     *
     * @param eventId The ID of the event.
     * @param userId  The ID of the user.
     * @return A composite Document ID string.
     */
    public static String createDocumentId(String eventId, String userId) {
        return eventId + "_" + userId;
    }

    /**
     * Get formatted join date.
     * @return Formatted date string (e.g., "Nov 02, 2024").
     */
    public String getFormattedJoinedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(joinedAt));
    }

    /**
     * Get formatted join date and time.
     * @return Formatted date-time string (e.g., "Nov 02, 2024 3:30 PM").
     */
    public String getFormattedJoinedAtWithTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        return sdf.format(new Date(joinedAt));
    }

    /**
     * Get time since joined in a human-readable format.
     * @return Relative time string (e.g., "2 hours ago", "3 days ago").
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
     * Check if user is waiting.
     * @return true if status is {@link #STATUS_WAITING}.
     */
    public boolean isWaiting() {
        return STATUS_WAITING.equals(status);
    }

    /**
     * Check if user was selected.
     * @return true if status is {@link #STATUS_SELECTED}.
     */
    public boolean isSelected() {
        return STATUS_SELECTED.equals(status);
    }

    /**
     * Check if user cancelled.
     * @return true if status is {@link #STATUS_CANCELLED}.
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    /**
     * Mark this entry as selected.
     * Changes the internal status to {@link #STATUS_SELECTED}.
     */
    public void markAsSelected() {
        this.status = STATUS_SELECTED;
    }

    /**
     * Mark this entry as cancelled.
     * Changes the internal status to {@link #STATUS_CANCELLED}.
     */
    public void markAsCancelled() {
        this.status = STATUS_CANCELLED;
    }

    /**
     * Check if user accepted their spot.
     * @return true if status is {@link #STATUS_ACCEPTED}.
     */
    public boolean isAccepted() {return STATUS_ACCEPTED.equals(status);}

    /**
     * Check if user declined their spot.
     * @return true if the status if {@link #STATUS_DECLINED}.
     */
    public boolean isDeclined() {return STATUS_DECLINED.equals(status);}

    /**
     * Mark this entry as accepted.
     * Changes the internal status to {@link #STATUS_ACCEPTED}.
     */
    public void markAsAccepted() {this.status = STATUS_ACCEPTED;}

    /**
     * Mark this entry as declined.
     * Changes the internal status to {@link #STATUS_DECLINED}.
     */
    public void markAsDeclined() {this.status = STATUS_DECLINED;}

    /**
     * Validate waitlist data before saving to database.
     * @return Error message string if invalid, or null if valid.
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
        // Check if the status is one of the predefined valid statuses
        if (!STATUS_WAITING.equals(status) &&
                !STATUS_SELECTED.equals(status) &&
                !STATUS_CANCELLED.equals(status) &&
                !STATUS_ACCEPTED.equals(status) &&
                !STATUS_DECLINED.equals(status)) {
            return "Invalid status value";
        }
        return null; // Valid
    }


    // ==================== Object Methods ====================

    /**
     * Provides a concise string representation of the waitlist entry, useful for logging.
     *
     * @return A string summary of the waitlist entry.
     */
    @Override
    public String toString() {
        return "Waitlist{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", joinedAt=" + getFormattedJoinedAt() +
                '}';
    }

    /**
     * Compares this waitlist entry to another object for equality.
     * Two entries are considered equal if their composite document IDs
     * (e.g., "eventId_userId") are identical.
     *
     * @param o The object to compare with.
     * @return True if the objects represent the same waitlist entry, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waitlist waitlist = (Waitlist) o;
        // Two entries are equal if their composite document ID is the same
        return getDocumentId().equals(waitlist.getDocumentId());
    }

    /**
     * Generates a hash code for the waitlist entry.
     * The hash code is based on the composite document ID.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return getDocumentId().hashCode();
    }
}