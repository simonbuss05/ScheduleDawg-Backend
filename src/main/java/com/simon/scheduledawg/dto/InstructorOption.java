package com.simon.scheduledawg.dto;

public class InstructorOption {

    private Long id;
    private String instructorName;
    private boolean syllabusAvailable;
    private String rmpSearchUrl;

    public InstructorOption() {

    }

    public InstructorOption(Long id, String instructorName, boolean syllabusAvailable, String rmpSearchUrl) {
        this.id = id;
        this.instructorName = instructorName;
        this.syllabusAvailable = syllabusAvailable;
        this.rmpSearchUrl = rmpSearchUrl;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getInstructorName() {
        return instructorName;
    }
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
    public boolean isSyllabusAvailable() {
        return syllabusAvailable;
    }
    public void setSyllabusAvailable(boolean syllabusAvailable) {
        this.syllabusAvailable = syllabusAvailable;
    }
    public String getRmpSearchUrl() {
        return rmpSearchUrl;
    }
    public void setRmpSearchUrl(String rmpSearchUrl) {
        this.rmpSearchUrl = rmpSearchUrl;
    }
}
