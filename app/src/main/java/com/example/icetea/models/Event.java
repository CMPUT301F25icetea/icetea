package com.example.icetea.models;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public Event() {
    }

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getLotteryProcess() { return lotteryProcess; }
    public void setLotteryProcess(String lotteryProcess) { this.lotteryProcess = lotteryProcess; }

    public int getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public int getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        Date date = createdAt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public String getFormattedCreatedAtWithTime() {
        if (createdAt == null) return "N/A";
        Date date = createdAt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    public boolean isWaitlistFull() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        return waitlistCount >= maxCapacity;
    }

    public int getRemainingSpots() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        return maxCapacity - waitlistCount;
    }

    public int getWaitlistFillPercentage() {
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        if (maxCapacity == 0) return 0;
        return (int) ((waitlistCount * 100.0) / maxCapacity);
    }

    public boolean canAcceptParticipants() {
        return isActive && !isWaitlistFull();
    }

    public String validate() {
        if (name == null || name.trim().isEmpty()) return "Event name is required";
        if (description == null || description.trim().isEmpty()) return "Event description is required";
        int maxCapacity = (capacity != null && capacity > 0) ? capacity : totalParticipants;
        if (maxCapacity <= 0) return "Event capacity must be greater than 0";
        if (lotteryProcess == null || lotteryProcess.trim().isEmpty()) return "Lottery process description is required";
        if (organizerId == null || organizerId.trim().isEmpty()) return "Organizer ID is required";
        return null;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
