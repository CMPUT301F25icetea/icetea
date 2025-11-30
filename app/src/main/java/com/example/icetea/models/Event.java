package com.example.icetea.models;

import com.google.firebase.Timestamp;

/**
 * Represents an event in the system.
 *
 * An Event encapsulates all necessary information for an event, including:
 * - Organizer
 * - Event details (name, description, criteria)
 * - Visual poster
 * - Registration period
 * - Event start and end times
 * - Location
 * - Participant limits
 * - Current state (entrants, drawing status, geolocation requirement)
 *
 * This class maps directly to Firestore documents in the "events" collection.
 */
public class Event {

    /**
     * The user ID of the organizer who created the event.
     */
    private String organizerId;

    /**
     * The unique ID of this event.
     */
    private String eventId;

    /**
     * The name/title of the event.
     */
    private String name;

    /**
     * A description of the event, including relevant information for participants.
     */
    private String description;

    /**
     * The criteria or requirements participants must meet to join this event.
     */
    private String criteria;

    /**
     * Base64-encoded string representing the event's poster image.
     */
    private String posterBase64;

    /**
     * Timestamp indicating when registration opens for this event.
     */
    private Timestamp registrationStartDate;

    /**
     * Timestamp indicating when registration closes for this event.
     */
    private Timestamp registrationEndDate;

    /**
     * Timestamp indicating when the event itself begins.
     */
    private Timestamp eventStartDate;

    /**
     * Timestamp indicating when the event itself ends.
     */
    private Timestamp eventEndDate;

    /**
     * The location (address or venue) where the event will be held.
     */
    private String location;

    /**
     * Maximum number of participants allowed in the event.
     */
    private Integer maxEntrants;

    /**
     * Current number of participants registered for the event.
     */
    private Integer currentEntrants;

    /**
     * Whether a geolocation requirement is enforced for participants to join.
     */
    private boolean geolocationRequirement;

    /**
     * Whether the event's drawing/lottery has already been conducted.
     */
    private boolean alreadyDrew;

    /**
     * Default constructor required by Firestore.
     */
    public Event() {
        // Firestore requires empty constructor
    }

    /**
     * Returns the ID of the user who created this event.
     *
     * @return the organizer's user ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the ID of the user who created this event.
     *
     * @param organizerId the organizer's user ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Returns the unique ID of this event.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique ID of this event.
     *
     * @param eventId the event ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the event name/title.
     *
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the event name/title.
     *
     * @param name the event name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the event description.
     *
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event description.
     *
     * @param description the event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the criteria required for participants.
     *
     * @return criteria for joining the event
     */
    public String getCriteria() {
        return criteria;
    }

    /**
     * Sets the criteria required for participants.
     *
     * @param criteria criteria for joining the event
     */
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    /**
     * Returns the Base64 string for the event's poster image.
     *
     * @return poster image in Base64
     */
    public String getPosterBase64() {
        return posterBase64;
    }

    /**
     * Sets the Base64 string for the event's poster image.
     *
     * @param posterBase64 poster image in Base64
     */
    public void setPosterBase64(String posterBase64) {
        this.posterBase64 = posterBase64;
    }

    /**
     * Returns the registration start timestamp.
     *
     * @return registration start timestamp
     */
    public Timestamp getRegistrationStartDate() {
        return registrationStartDate;
    }

    /**
     * Sets the registration start timestamp.
     *
     * @param registrationStartDate registration start timestamp
     */
    public void setRegistrationStartDate(Timestamp registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    /**
     * Returns the registration end timestamp.
     *
     * @return registration end timestamp
     */
    public Timestamp getRegistrationEndDate() {
        return registrationEndDate;
    }

    /**
     * Sets the registration end timestamp.
     *
     * @param registrationEndDate registration end timestamp
     */
    public void setRegistrationEndDate(Timestamp registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    /**
     * Returns the event start timestamp.
     *
     * @return event start timestamp
     */
    public Timestamp getEventStartDate() {
        return eventStartDate;
    }

    /**
     * Sets the event start timestamp.
     *
     * @param eventStartDate event start timestamp
     */
    public void setEventStartDate(Timestamp eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    /**
     * Returns the event end timestamp.
     *
     * @return event end timestamp
     */
    public Timestamp getEventEndDate() {
        return eventEndDate;
    }

    /**
     * Sets the event end timestamp.
     *
     * @param eventEndDate event end timestamp
     */
    public void setEventEndDate(Timestamp eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    /**
     * Returns the event location.
     *
     * @return event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the event location.
     *
     * @param location event location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the maximum number of participants allowed.
     *
     * @return maximum entrants
     */
    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    /**
     * Sets the maximum number of participants allowed.
     *
     * @param maxEntrants maximum entrants
     */
    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    /**
     * Returns the current number of participants registered.
     *
     * @return current entrants
     */
    public Integer getCurrentEntrants() {
        return currentEntrants;
    }

    /**
     * Sets the current number of participants registered.
     *
     * @param currentEntrants current entrants
     */
    public void setCurrentEntrants(Integer currentEntrants) {
        this.currentEntrants = currentEntrants;
    }

    /**
     * Returns whether geolocation is required for joining.
     *
     * @return true if geolocation is required, false otherwise
     */
    public boolean getGeolocationRequirement() {
        return geolocationRequirement;
    }

    /**
     * Sets whether geolocation is required for joining.
     *
     * @param geolocationRequirement true if geolocation is required, false otherwise
     */
    public void setGeolocationRequirement(boolean geolocationRequirement) {
        this.geolocationRequirement = geolocationRequirement;
    }

    /**
     * Returns whether the event's drawing has already been conducted.
     *
     * @return true if drawing already occurred, false otherwise
     */
    public boolean getAlreadyDrew() {
        return alreadyDrew;
    }

    /**
     * Sets whether the event's drawing has already been conducted.
     *
     * @param alreadyDrew true if drawing already occurred, false otherwise
     */
    public void setAlreadyDrew(boolean alreadyDrew) {
        this.alreadyDrew = alreadyDrew;
    }
}