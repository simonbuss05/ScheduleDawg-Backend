package com.simon.scheduledawg.syllabus;

import java.util.List;

public class ExtractedMeeting {

    private List<String> days;
    private String startTime;
    private String endTime;
    private String building;
    private String room;

    public ExtractedMeeting() {

    }
    public ExtractedMeeting(List<String> days, String startTime, String endTime, String building, String room) {
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.building = building;
        this.room = room;
    }
    public List<String> getDays() {
        return days;
    }
    public void setDays(List<String> days) {
        this.days = days;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getBuilding() {
        return building;
    }
    public void setBuilding(String building) {
        this.building = building;
    }
    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }
}
