package com.example.icetea.event;

import com.google.firebase.Timestamp;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

    public Event() {
        // Firestore requires empty constructor
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(Timestamp registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public Timestamp getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(Timestamp registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
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
