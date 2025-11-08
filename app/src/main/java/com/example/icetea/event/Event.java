package com.example.icetea.event;

import com.google.firebase.Timestamp;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Represents an event in the system.
 *
 * Contains information such as the event name, description, organizer, location,
 * capacity, start and end times, registration period, attendees, and waiting list.
 */
public class Event {

    private String id;
    private String name;
    private String description;
    private String organizerId;
    private String location;
    private Integer capacity;

    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp registrationStartDate;
    private Timestamp registrationEndDate;

    private List<String> waitingList;
    private List<String> attendees;

    private boolean registrationOpen;
    private String posterUrl;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Event() {
        // Firestore requires empty constructor
    }

    /**
     * Constructs a new Event with all properties specified.
     *
     * @param id Event identifier
     * @param organizerId Organizer's user ID
     * @param name Event name
     * @param description Event description
     * @param location Event location
     * @param capacity Maximum number of attendees
     * @param startDate Event start timestamp
     * @param endDate Event end timestamp
     * @param registrationStartDate Registration start timestamp
     * @param registrationEndDate Registration end timestamp
     * @param posterUrl URL for the event poster
     * @param waitingList List of user IDs on the waiting list
     * @param attendees List of user IDs who are attending
     */
    public Event(String id, String organizerId, String name,
                 String description, String location, Integer capacity,
                 Timestamp startDate, Timestamp endDate,
                 Timestamp registrationStartDate, Timestamp registrationEndDate,
                 String posterUrl,
                 List<String> waitingList, List<String> attendees) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationStartDate = registrationStartDate;
        this.registrationEndDate = registrationEndDate;
        this.posterUrl = posterUrl;
        this.waitingList = waitingList;
        this.attendees = attendees;
    }

    // --- Getters and Setters with Javadoc ---

    /** @return the event ID */
    public String getId() {
        return id;
    }

    /** Sets the event ID */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the event name */
    public String getName() {
        return name;
    }

    /** Sets the event name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the event description */
    public String getDescription() {
        return description;
    }

    /** Sets the event description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the organizer's user ID */
    public String getOrganizerId() {
        return organizerId;
    }

    /** Sets the organizer's user ID */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /** @return the location of the event */
    public String getLocation() {
        return location;
    }

    /** Sets the location of the event */
    public void setLocation(String location) {
        this.location = location;
    }

    /** @return the maximum capacity of the event */
    public Integer getCapacity() {
        return capacity;
    }

    /** Sets the maximum capacity of the event */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /** @return the event start timestamp */
    public Timestamp getStartDate() {
        return startDate;
    }

    /** Sets the event start timestamp */
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /** @return the event end timestamp */
    public Timestamp getEndDate() {
        return endDate;
    }

    /** Sets the event end timestamp */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /** @return the registration start timestamp */
    public Timestamp getRegistrationStartDate() {
        return registrationStartDate;
    }

    /** Sets the registration start timestamp */
    public void setRegistrationStartDate(Timestamp registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    /** @return the registration end timestamp */
    public Timestamp getRegistrationEndDate() {
        return registrationEndDate;
    }

    /** Sets the registration end timestamp */
    public void setRegistrationEndDate(Timestamp registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    /** @return the waiting list as a list of user IDs */
    public List<String> getWaitingList() {
        return waitingList;
    }

    /** Sets the waiting list */
    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    /** @return the attendees list as a list of user IDs */
    public List<String> getAttendees() {
        return attendees;
    }

    /** Sets the attendees list */
    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    /** @return whether registration is open */
    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    /** Sets whether registration is open */
    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    /** @return the URL of the poster image */
    public String getPosterUrl() {
        return posterUrl;
    }

    /** Sets the poster URL */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * Formats a Firebase Timestamp into a human-readable string (e.g., "January 1, 2025").
     *
     * @param timestamp The Firebase Timestamp
     * @return Formatted string, or "N/A" if timestamp is null
     */
    public static String formatTimestampHumanReadable(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat("MMMM d, yyyy", Locale.US).format(timestamp.toDate());
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", organizerId='" + organizerId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", registrationStartDate=" + registrationStartDate +
                ", registrationEndDate=" + registrationEndDate +
                ", capacity=" + capacity +
                '}';
    }
}
