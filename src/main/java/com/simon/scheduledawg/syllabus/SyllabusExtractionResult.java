package com.simon.scheduledawg.syllabus;

import java.util.List;

public class SyllabusExtractionResult {

    private ExtractedCourse course;
    private List<ExtractedMeeting> meetings;
    private List<ExtractedAssignment> assignments;
    private List<ExtractedEvent> events;
    private List<ExtractedFinal> finals;


    public SyllabusExtractionResult() {

    }


    public SyllabusExtractionResult(ExtractedCourse course, List<ExtractedMeeting> meetings, List<ExtractedAssignment> assignments, List<ExtractedFinal> finals, List<ExtractedEvent> events) {
        this.course = course;
        this.meetings = meetings;
        this.assignments = assignments;
        this.events = events;
        this.finals = finals;
    }


    public ExtractedCourse getCourse() {
        return course;
    }
    public void setCourse(ExtractedCourse course) {
        this.course = course;
    }
    public List<ExtractedMeeting> getMeetings() {
        return meetings;
    }
    public void setMeetings(List<ExtractedMeeting> meetings) {
        this.meetings = meetings;
    }
    public List<ExtractedAssignment> getAssignments() {
        return assignments;
    }
    public void setAssignments(List<ExtractedAssignment> assignments) {
        this.assignments = assignments;
    }
    public List<ExtractedFinal> getFinals() {
        return finals;
    }
    public void setFinals(List<ExtractedFinal> finals) {
        this.finals = finals;
    }
    public List<ExtractedEvent> getEvents() {
        return events;
    }
    public void setEvents(List<ExtractedEvent> events) {
        this.events = events;
    }
}
