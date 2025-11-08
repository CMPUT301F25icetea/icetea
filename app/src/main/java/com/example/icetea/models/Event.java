package com.example.icetea.models;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents an event in the system with details about capacity, participants, and waitlist.
 * This class manages event information including name, description, organizer, and participant counts.
 */
public class Event {
    private String id;
    private String name;
    private String description;
    private String organizerId;
    private boolean isActive;
    private String lotteryProcess;
    private int totalParticipants;
    private Integer capacity;
    private int waitlistCount;
    private Timestamp createdAt;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Event() {
    }

    /**
     * Constructs a new Event with the specified details.
     *
     * @param id             The unique identifier for the event
     * @param name           The name of the event
     * @param description    A description of the event
     * @param capacity       The maximum number of participants allowed
     * @param lotteryProcess Description of how the lottery selection works
     * @param organizerId    The ID of the user organizing this event
     */
    public Event(String id, String name, String description, int capacity,
                 String lotteryProcess, String organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.totalParticipants = capacity;
        this.waitlistCount = 0;
        this.lotteryProcess = lotteryProcess;
        this.organizerId = organizerId;
        this.createdAt = new Timestamp(new Date());
        this.isActive = true;
    }

    /**
     * Gets the unique identifier of the event.
     *
     * @return The event ID
     */
    public String getId() { return id; }

    /**
     * Sets the unique identifier of the event.
     *
     * @param id The event ID to set
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the name of the event.
     *
     * @return The event name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the event.
     *
     * @param name The event name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the description of the event.
     *
     * @return The event description
     */
    public String getDescription() { return description; }

    /**
     * Sets the description of the event.
     *
     * @param description The event description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the ID of the organizer who created this event.
     *
     * @return The organizer's user ID
     */
    public String getOrganizerId() { return organizerId; }

    /**
     * Sets the ID of the organizer who created this event.
     *
     * @param organizerId The organizer's user ID to set
     */
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    /**
     * Checks if the event is currently active.
     *
     * @return true if the event is active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets whether the event is active.
     *
     * @param active true to activate the event, false to deactivate
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Gets the description of how the lottery process works for this event.
     *
     * @return The lottery process description
     */
    public String getLotteryProcess() { return lotteryProcess; }

    /**
     * Sets the description of how the lottery process works for this event.
     *
     * @param lotteryProcess The lottery process description to set
     */
    public void setLotteryProcess(String lotteryProcess) { this.lotteryProcess = lotteryProcess; }

    /**
     * Gets the total number of participants allowed for this event.
     *
     * @return The total participant count
     */
    public int getTotalParticipants() { return totalParticipants; }

    /**
     * Sets the total number of participants allowed for this event.
     *
     * @param totalParticipants The total participant count to set
     */
    public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }

    /**
     * Gets the capacity (maximum number of participants) for this event.
     *
     * @return The event capacity, or null if not set
     */
    public Integer getCapacity() { return capacity; }

    /**
     * Sets the capacity (maximum number of participants) for this event.
     *
     * @param capacity The event capacity to set
     */
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    /**
     * Gets the current number of people on the waitlist.
     *
     * @return The waitlist count
     */
    public int getWaitlistCount() { return waitlistCount; }

    /**
     * Sets the current number of people on the waitlist.
     *
     * @param waitlistCount The waitlist count to set
     */
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }

    /**
     * Gets the timestamp when this event was created.
     *
     * @return The creation timestamp
     */
    public Timestamp getCreatedAt() { return createdAt; }

    /**
     * Sets the timestamp when this event was created.
     *
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Gets the formatted creation date without time.
     *
     * @return A formatted date string (e.g., "Jan 15, 2024") or "N/A" if not set
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        Date date = createdAt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Gets the formatted creation date with time.
     *
     * @return A formatted date-time string (e.g., "Jan 15, 2024 3:45 PM") or "N/A" if not set
     */
    public String getFormattedCreatedAtWithTime() {
        if (createdAt == null) return "N/A";
        Date date = createdAt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Checks if the waitlist is full based on capacity or total participants.
     *
     * @return true if the waitlist has reached maximum capacity, false otherwise
     */
    public boolean isWaitlistFull() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        return waitlistCount >= maxCapacity;
    }

    /**
     * Calculates the number of remaining spots available on the waitlist.
     *
     * @return The number of spots still available
     */
    public int getRemainingSpots() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        return maxCapacity - waitlistCount;
    }

    /**
     * Calculates the percentage of the waitlist that is filled.
     *
     * @return The fill percentage (0-100)
     */
    public int getWaitlistFillPercentage() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        if (maxCapacity == 0) return 0;
        return (int) ((waitlistCount * 100.0) / maxCapacity);
    }

    /**
     * Checks if this event can currently accept new participants.
     * An event can accept participants if it's active and the waitlist is not full.
     *
     * @return true if the event can accept participants, false otherwise
     */
    public boolean canAcceptParticipants() {
        return isActive && !isWaitlistFull();
    }

    /**
     * Validates the event data and returns any validation errors.
     *
     * @return An error message if validation fails, or null if all data is valid
     */
    public String validate() {
        if (name == null || name.trim().isEmpty()) return "Event name is required";
        if (description == null || description.trim().isEmpty()) return "Event description is required";
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        if (maxCapacity <= 0) return "Event capacity must be greater than 0";
        if (lotteryProcess == null || lotteryProcess.trim().isEmpty()) return "Lottery process description is required";
        if (organizerId == null || organizerId.trim().isEmpty()) return "Organizer ID is required";
        return null;
    }

    /**
     * Returns a string representation of this event.
     *
     * @return A string containing the event's key details
     */
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", totalParticipants=" + totalParticipants +
                ", waitlistCount=" + waitlistCount +
                ", isActive=" + isActive +
                ", createdAt=" + getFormattedCreatedAt() +
                '}';
    }

    /**
     * Checks if this event is equal to another object.
     * Two events are considered equal if they have the same ID.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.id);
    }

    /**
     * Generates a hash code for this event based on its ID.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}