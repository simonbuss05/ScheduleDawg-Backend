package com.simon.scheduledawg.syllabus;

public class ExtractedFinal {
    private String title;
    private String date;
    private String endTime;
    private String startTime;
    private String location;

    public ExtractedFinal() {

    }

    public ExtractedFinal(String title, String date, String endTime, String startTime, String location) {
        this.title = title;
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        this.location = location;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
