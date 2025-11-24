package com.example.icetea.models;

import com.google.firebase.Timestamp;

import java.sql.Time;
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

    /** The ID of the user on the waitlist. */
    private String userId;
    /** The ID of the event this waitlist entry belongs to. */
    private String eventId;
    /** The timestamp (in milliseconds) when the user joined the waitlist. */
    private Timestamp timestamp;
    /** The current status of the waitlist entry (e.g., "waiting", "selected"). */
    private String status;
    private Double latitude;
    private Double longitude;


    /**
     * Required empty constructor for Firestore deserialization
     */
    public Waitlist() {
        // Firestore requires this
    }

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
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The join timestamp (in milliseconds).
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return userId + "_" + eventId;
    }
}