package com.example.icetea.event;

import com.google.firebase.Timestamp;

import java.util.List;

/**
 * Represents an event in the system.
 *
 * Contains information such as the event name, description, organizer, location,
 * capacity, start and end times, registration period, attendees, and waiting list.
 */
public class Event {
    private String organizerId;
    private String eventId;
    private String eventName;
    private String eventDescription;
    private String eventCriteria;
    private String posterBase64;
    private Timestamp registrationStartDate;
    private Timestamp registrationEndDate;
    private Timestamp eventStartDate;
    private Timestamp eventEndDate;
    private String eventLocation;
    private Integer maxEntrants;
    private boolean geolocationRequirement;

    public Event() {
        // Firestore requires empty constructor
    }

    public String getEventCriteria() {
        return eventCriteria;
    }

    public void setEventCriteria(String eventCriteria) {
        this.eventCriteria = eventCriteria;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Timestamp getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Timestamp eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Timestamp getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Timestamp eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public boolean getGeolocationRequirement() {
        return geolocationRequirement;
    }

    public void setGeolocationRequirement(boolean geolocationRequirement) {
        this.geolocationRequirement = geolocationRequirement;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getPosterBase64() {
        return posterBase64;
    }

    public void setPosterBase64(String posterBase64) {
        this.posterBase64 = posterBase64;
    }

    public Timestamp getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(Timestamp registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public Timestamp getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(Timestamp registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }
}
