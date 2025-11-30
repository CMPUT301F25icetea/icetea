package com.example.icetea.models;

import com.google.firebase.Timestamp;

/**
 * Represents a single waitlist entry for a user in an event.
 * This class maps to documents in the Firestore 'waitlist' collection.
 *
 * <p>Firestore Collection Path: /waitlist/{userId}_{eventId}</p>
 *
 * <p>Database Schema:</p>
 * <ul>
 *     <li>eventId: String – reference to the event</li>
 *     <li>userId: String – ID of the user who joined the waitlist</li>
 *     <li>timestamp: Timestamp – when the user joined the waitlist</li>
 *     <li>status: String – status of the waitlist entry (waiting/selected/cancelled/etc.)</li>
 *     <li>latitude, longitude: Double – optional geolocation of the user</li>
 *     <li>replaced: boolean – whether the user was replaced in the waitlist</li>
 * </ul>
 */
public class Waitlist {

    /** User is currently on the waitlist and awaiting selection. */
    public static final String STATUS_WAITING = "waiting";

    /** User has been selected by the event organizer from the waitlist. */
    public static final String STATUS_SELECTED = "selected";

    /** User voluntarily left the waitlist or event. */
    public static final String STATUS_CANCELLED = "cancelled";

    /** User was selected and accepted the spot. */
    public static final String STATUS_ACCEPTED = "accepted";

    /** User was selected but declined the spot. */
    public static final String STATUS_DECLINED = "declined";

    /** User ID of the waitlist entry. */
    private String userId;

    /** Event ID associated with this waitlist entry. */
    private String eventId;

    /** Timestamp of when the user joined the waitlist. */
    private Timestamp timestamp;

    /** Current status of the waitlist entry. */
    private String status;

    /** Optional latitude of the user. */
    private Double latitude;

    /** Optional longitude of the user. */
    private Double longitude;

    /** Indicates whether the user was replaced by another in the waitlist. */
    private boolean replaced;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Waitlist() {
        // Firestore requires this
    }

    /**
     * @return The ID of the event associated with this waitlist entry.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID for this waitlist entry.
     * @param eventId Event ID.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * @return The ID of the user in this waitlist entry.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for this waitlist entry.
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return Timestamp representing when the user joined the waitlist.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for when the user joined the waitlist.
     * @param timestamp Timestamp object.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Current status of this waitlist entry (e.g., "waiting").
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of this waitlist entry.
     * @param status Status string.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Latitude of the user's location, or null if not set.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the user's location.
     * @param latitude Latitude value.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return Longitude of the user's location, or null if not set.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the user's location.
     * @param longitude Longitude value.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return True if the user was replaced in the waitlist; false otherwise.
     */
    public boolean getReplaced() {
        return replaced;
    }

    /**
     * Sets the replaced status of this waitlist entry.
     * @param replaced True if replaced, false otherwise.
     */
    public void setReplaced(boolean replaced) {
        this.replaced = replaced;
    }

    /**
     * @return A unique ID for this waitlist entry in the format "{userId}_{eventId}".
     */
    public String getId() {
        return userId + "_" + eventId;
    }
}
