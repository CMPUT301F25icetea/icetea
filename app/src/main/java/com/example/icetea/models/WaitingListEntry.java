package com.example.icetea.models;

public class WaitingListEntry {
    private String userId;
    private String email;
    private String status;
    private String joinTime;
    private boolean isSelected;

    public WaitingListEntry(String userId, String email, String status, String joinTime, boolean isSelected) {
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.joinTime = joinTime;
        this.isSelected = isSelected;
    }

    public WaitingListEntry() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
