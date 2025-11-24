package com.example.icetea.models;

import com.google.firebase.Timestamp;

/**
 * Represents an event in the system.
 *
 * Contains information such as the event name, description, organizer, location,
 * capacity, start and end times, registration period, attendees, and waiting list.
 */
public class Event {
    private String organizerId;
    private String eventId;
    private String name;
    private String description;
    private String criteria;
    private String posterBase64;
    private Timestamp registrationStartDate;
    private Timestamp registrationEndDate;
    private Timestamp eventStartDate;
    private Timestamp eventEndDate;
    private String location;
    private Integer maxEntrants;
    private Integer currentEntrants;
    private boolean geolocationRequirement;
    private boolean alreadyDrew;

    public Event() {
        // Firestore requires empty constructor
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxEntrants() {
        return maxEntrants;
    }
    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean getAlreadyDrew() {
        return this.alreadyDrew;
    }

    public void setAlreadyDrew(boolean alreadyDrew) {
        this.alreadyDrew = alreadyDrew;
    }

    public Integer getCurrentEntrants() {
        return currentEntrants;
    }

    public void setCurrentEntrants(Integer currentEntrants) {
        this.currentEntrants = currentEntrants;
    }
}
