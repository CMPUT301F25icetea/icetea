package com.example.icetea.models;

public class WaitingListEntry {
    private String email;
    private String status;
    private String joinTime;
    private boolean isSelected;

    public WaitingListEntry(String email, String status, String joinTime, boolean isSelected) {
        this.email = email;
        this.status = status;
        this.joinTime = joinTime;
        this.isSelected = isSelected;
    }

    public WaitingListEntry() {

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
