package com.example.icetea.models;

/**
 * Represents an entry in a waiting list for an event.
 * Each entry corresponds to a user who has joined the waiting list.
 */
public class WaitingListEntry {
    /** The ID of the user on the waiting list. */
    private String userId;

    /** The ID of the event the user is waiting for. */
    private String eventId;

    /** The email address of the user. */
    private String email;

    /** The current status of the user in the waiting list (e.g., "invited", "cancelled"). */
    private String status;

    /** The timestamp when the user joined the waiting list, stored as a formatted string. */
    private String joinTime;

    /** Whether this entry is currently selected (used in UI lists). */
    private boolean isSelected;

    /**
     * Constructs a new WaitingListEntry with all fields specified.
     *
     * @param userId The ID of the user.
     * @param email The user's email.
     * @param status The status of the user on the waiting list.
     * @param joinTime The formatted join time of the user.
     * @param isSelected Whether the entry is selected.
     */
    public WaitingListEntry(String userId, String email, String status, String joinTime, boolean isSelected) {
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.joinTime = joinTime;
        this.isSelected = isSelected;
    }

    /**
     * Default constructor required for Firestore or serialization.
     */
    public WaitingListEntry() {

    }

    /**
     * Returns the ID of the user.
     *
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user.
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the ID of the event.
     *
     * @return event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event.
     *
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the user's email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the current status of the user in the waiting list.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the user in the waiting list.
     *
     * @param status The status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the formatted join time of the user.
     *
     * @return join time
     */
    public String getJoinTime() {
        return joinTime;
    }

    /**
     * Sets the formatted join time of the user.
     *
     * @param joinTime The join time to set
     */
    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    /**
     * Returns whether this waiting list entry is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets whether this waiting list entry is selected.
     *
     * @param selected true to mark as selected, false otherwise
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
