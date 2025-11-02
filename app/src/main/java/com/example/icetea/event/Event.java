package com.example.icetea.event;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class Event {

    private String id, name, description, organizerId, location;
    Date startTime, endTime, registrationStartTime, registrationEndTime;

    List<String> waitingList;

    boolean registrationOpen;

    public Event() {
        //req
    }

    public Event(String id, String name, String description, String organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.organizerId = organizerId;
    }

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

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", organizerID='" + organizerId + '\'' +
                '}';
    }
}
