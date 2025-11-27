package com.example.icetea.history;

import com.example.icetea.models.Event;

public class HistoryEventItem {
    private Event event;
    private String waitlistStatus;

    public HistoryEventItem(Event event, String waitlistStatus) {
        this.event = event;
        this.waitlistStatus = waitlistStatus;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public void setWaitlistStatus(String waitlistStatus) {
        this.waitlistStatus = waitlistStatus;
    }
}