package com.example.icetea.history;

import com.example.icetea.models.Event;

/**
 * A wrapper class representing an event in the user's history along with
 * its waitlist status.
 */
public class HistoryEventItem {

    /** The event associated with this history item. */
    private Event event;

    /** The waitlist status of the user for this event (e.g., "Registered", "Waitlisted"). */
    private String waitlistStatus;

    /**
     * Constructs a new HistoryEventItem with the given event and waitlist status.
     *
     * @param event the event to associate with this history item
     * @param waitlistStatus the waitlist status for the event
     */
    public HistoryEventItem(Event event, String waitlistStatus) {
        this.event = event;
        this.waitlistStatus = waitlistStatus;
    }

    /**
     * Gets the event associated with this history item.
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the event associated with this history item.
     *
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets the waitlist status of this history item.
     *
     * @return the waitlist status
     */
    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    /**
     * Sets the waitlist status of this history item.
     *
     * @param waitlistStatus the waitlist status to set
     */
    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }
}