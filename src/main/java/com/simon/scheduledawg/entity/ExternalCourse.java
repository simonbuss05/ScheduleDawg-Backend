package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// A course as scraped from the UGA bulletin, shared across every user — not
// tied to any one account. Cached with a TTL (see BulletinScraperService)
// rather than re-scraped on every request.
@Entity
@Table(name = "external_courses")
public class ExternalCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bulletinCourseId;

    private String subjectCode;

    private String courseNumber;

    private String title;

    private LocalDateTime lastScrapedAt;

    @OneToMany(mappedBy = "externalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ExternalCourseInstructor> instructors = new ArrayList<>();

    public ExternalCourse() {

    }

    public ExternalCourse(Long bulletinCourseId, String subjectCode, String courseNumber, String title, LocalDateTime lastScrapedAt) {
        this.bulletinCourseId = bulletinCourseId;
        this.subjectCode = subjectCode;
        this.courseNumber = courseNumber;
        this.title = title;
        this.lastScrapedAt = lastScrapedAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getBulletinCourseId() {
        return bulletinCourseId;
    }
    public void setBulletinCourseId(Long bulletinCourseId) {
        this.bulletinCourseId = bulletinCourseId;
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDateTime getLastScrapedAt() {
        return lastScrapedAt;
    }
    public void setLastScrapedAt(LocalDateTime lastScrapedAt) {
        this.lastScrapedAt = lastScrapedAt;
    }
    public List<ExternalCourseInstructor> getInstructors() {
        return instructors;
    }
    public void setInstructors(List<ExternalCourseInstructor> instructors) {
        this.instructors = instructors;
    }
}
