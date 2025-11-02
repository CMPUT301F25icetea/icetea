package com.example.icetea.entrant;

import com.google.firebase.Timestamp;

public class Event {
    private String id;
    private String name;
    private String description;
    private String organizerId;
    private boolean isActive;
    private String lotteryProcess;
    private int totalParticipants;
    private int waitlistCount;
    private Timestamp createdAt;

    public Event() {
        // Required empty constructor for Firestore
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOrganizerId() { return organizerId; }
    public boolean isActive() { return isActive; }
    public String getLotteryProcess() { return lotteryProcess; }
    public int getTotalParticipants() { return totalParticipants; }
    public int getWaitlistCount() { return waitlistCount; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public void setActive(boolean active) { isActive = active; }
    public void setLotteryProcess(String lotteryProcess) { this.lotteryProcess = lotteryProcess; }
    public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}