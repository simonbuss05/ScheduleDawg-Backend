package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "planned_courses")
public class PlannedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String subjectCode;

    private String courseNumber;

    private String termLabel;

    private LocalDateTime createdAt;

    public PlannedCourse() {

    }

    public PlannedCourse(User user, String subjectCode, String courseNumber, String termLabel, LocalDateTime createdAt) {
        this.user = user;
        this.subjectCode = subjectCode;
        this.courseNumber = courseNumber;
        this.termLabel = termLabel;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getSubjectCode() {
        return subjectCode;
    }
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
    public String getCourseNumber() {
        return courseNumber;
    }
    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }
    public String getTermLabel() {
        return termLabel;
    }
    public void setTermLabel(String termLabel) {
        this.termLabel = termLabel;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
