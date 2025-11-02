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
    private String id;
    private String name;
    private String description;
    private int totalParticipants;
    private int waitlistCount;
    private String lotteryProcess;
    private String organizerId;

    // 🔥 FIX: Type changed from 'long' to 'Date'
    private Date createdAt;

    private boolean isActive;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public int getWaitlistCount() {
        return waitlistCount;
    }

    public void setWaitlistCount(int waitlistCount) {
        this.waitlistCount = waitlistCount;
    }

    public String getLotteryProcess() {
        return lotteryProcess;
    }

    public void setLotteryProcess(String lotteryProcess) {
        this.lotteryProcess = lotteryProcess;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    // 🔥 FIX: Getter updated to return Date
    public Date getCreatedAt() {
        return createdAt;
    }

    // 🔥 FIX: Setter updated to accept Date
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}