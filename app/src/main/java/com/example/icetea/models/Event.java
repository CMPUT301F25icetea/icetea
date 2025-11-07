package com.example.icetea.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Event model representing an event in the Firestore database.
 * Firestore Collection Path: /events/{eventId}
 * Database Schema:
 * - id: String (auto-generated unique identifier)
 * - name: String (event name)
 * - description: String (event description)
 * - totalParticipants: int (max number of participants)
 * - waitlistCount: int (current number in waitlist)
 * - lotteryProcess: String (lottery selection description)
 * - organizerId: String (creator's user ID)
 * - createdAt: Date (converted from Firestore Timestamp)
 * - isActive: boolean (event status)
 */
public class Event {

    // ==================== Fields ====================

    /** The unique identifier for the event (matches Firestore document ID). */
    private String id;
    /** The public name of the event. */
    private String name;
    /** A detailed description of the event. */
    private String description;
    /** The maximum number of participants allowed in the event. */
    private int totalParticipants;
    /** The current number of users on the waitlist. */
    private int waitlistCount;
    /** A text description of how participants will be selected. */
    private String lotteryProcess;
    /** The user ID (UID) of the event's creator/organizer. */
    private String organizerId;

    /** The timestamp when the event was created in the database. */
    // 🔥 FIX: Type changed from 'long' to 'Date'
    private Date createdAt;

    /** Flag indicating if the event is currently active or closed. */
    private boolean isActive;

    // ==================== Constructors ====================

    /**
     * Required empty constructor for Firestore deserialization
     */
    public Event() {
        // Firestore requires this
    }

    /**
     * Constructor for creating a new event
     *
     * @param id Event ID (pass null for auto-generation)
     * @param name Event name
     * @param description Event description
     * @param totalParticipants Maximum number of participants
     * @param lotteryProcess Lottery selection process description
     * @param organizerId User ID of the organizer
     */
    public Event(String id, String name, String description, int totalParticipants,
                 String lotteryProcess, String organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalParticipants = totalParticipants;
        this.waitlistCount = 0;
        this.lotteryProcess = lotteryProcess;
        this.organizerId = organizerId;

        // 🔥 FIX: Set as a new Date() object, not a long
        this.createdAt = new Date(System.currentTimeMillis());

        this.isActive = true;
    }

    // ==================== Getters and Setters ====================

    /**
     * @return The unique event ID.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The unique event ID (typically set by Firestore).
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The public name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The public name of the event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The detailed description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The detailed description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The maximum number of participants allowed.
     */
    public int getTotalParticipants() {
        return totalParticipants;
    }

    /**
     * @param totalParticipants The maximum number of participants allowed.
     */
    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    /**
     * @return The current number of users on the waitlist.
     */
    public int getWaitlistCount() {
        return waitlistCount;
    }

    /**
     * @param waitlistCount The current number of users on the waitlist.
     */
    public void setWaitlistCount(int waitlistCount) {
        this.waitlistCount = waitlistCount;
    }

    /**
     * @return The text description of the lottery process.
     */
    public String getLotteryProcess() {
        return lotteryProcess;
    }

    /**
     * @param lotteryProcess The text description of the lottery process.
     */
    public void setLotteryProcess(String lotteryProcess) {
        this.lotteryProcess = lotteryProcess;
    }

    /**
     * @return The user ID of the event organizer.
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * @param organizerId The user ID of the event organizer.
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * @return The creation timestamp of the event.
     */
    // 🔥 FIX: Getter updated to return Date
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The creation timestamp of the event.
     */
    // 🔥 FIX: Setter updated to accept Date
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return True if the event is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @param active Set to true to make the event active, false to close it.
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    // ==================== Helper Methods ====================

    /**
     * Get formatted creation date
     * @return Formatted date string (e.g., "Nov 02, 2024")
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        // 🔥 FIX: Use the 'createdAt' Date object directly
        return sdf.format(createdAt);
    }

    /**
     * Get formatted creation date and time
     * @return Formatted date-time string (e.g., "Nov 02, 2024 3:30 PM")
     */
    public String getFormattedCreatedAtWithTime() {
        if (createdAt == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());

        // 🔥 FIX: Use the 'createdAt' Date object directly
        return sdf.format(createdAt);
    }

    /**
     * Check if waitlist is full (reached total participants limit)
     * @return true if waitlist count equals or exceeds total participants
     */
    public boolean isWaitlistFull() {
        return waitlistCount >= totalParticipants;
    }

    /**
     * Get remaining spots in waitlist
     * @return Number of remaining spots (0 if full or negative if over capacity)
     */
    public int getRemainingSpots() {
        return totalParticipants - waitlistCount;
    }

    /**
     * Get waitlist fill percentage
     * @return Percentage (0-100) of how full the waitlist is
     */
    public int getWaitlistFillPercentage() {
        if (totalParticipants == 0) return 0;
        return (int) ((waitlistCount * 100.0) / totalParticipants);
    }

    /**
     * Check if the event can accept more participants
     * @return true if there are available spots and event is active
     */
    public boolean canAcceptParticipants() {
        return isActive && !isWaitlistFull();
    }

    /**
     * Validate event data
     * @return Error message if invalid, null if valid
     */
    public String validate() {
        if (name == null || name.trim().isEmpty()) {
            return "Event name is required";
        }
        if (description == null || description.trim().isEmpty()) {
            return "Event description is required";
        }
        if (totalParticipants <= 0) {
            return "Total participants must be greater than 0";
        }
        if (lotteryProcess == null || lotteryProcess.trim().isEmpty()) {
            return "Lottery process description is required";
        }
        if (organizerId == null || organizerId.trim().isEmpty()) {
            return "Organizer ID is required";
        }
        return null; // Valid
    }

    // ==================== Object Methods ====================

    /**
     * Provides a concise string representation of the event, useful for logging.
     *
     * @return A string summary of the event.
     */
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalParticipants=" + totalParticipants +
                ", waitlistCount=" + waitlistCount +
                ", isActive=" + isActive +
                ", createdAt=" + getFormattedCreatedAt() +
                '}';
    }

    /**
     * Compares this event to another object for equality.
     * Two events are considered equal if their {@link #id} fields are non-null and identical.
     *
     * @param o The object to compare with.
     * @return True if the objects are the same event (based on ID), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        // Two events are equal if their IDs are non-null and equal
        return id != null && id.equals(event.id);
    }

    /**
     * Generates a hash code for the event, based primarily on its unique {@link #id}.
     *
     * @return The hash code, or 0 if the id is null.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}