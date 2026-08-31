package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "external_course_instructors")
public class ExternalCourseInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "external_course_id")
    @JsonIgnore
    private ExternalCourse externalCourse;

    private String instructorName;

    // UGA's own id for this instructor's syllabus file on the bulletin site;
    // null if they don't have one on file.
    private Long syllabusFileId;

    private LocalDateTime lastScrapedAt;

    public ExternalCourseInstructor() {

    }

    public ExternalCourseInstructor(ExternalCourse externalCourse, String instructorName, Long syllabusFileId, LocalDateTime lastScrapedAt) {
        this.externalCourse = externalCourse;
        this.instructorName = instructorName;
        this.syllabusFileId = syllabusFileId;
        this.lastScrapedAt = lastScrapedAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ExternalCourse getExternalCourse() {
        return externalCourse;
    }
    public void setExternalCourse(ExternalCourse externalCourse) {
        this.externalCourse = externalCourse;
    }
    public String getInstructorName() {
        return instructorName;
    }
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
    public Long getSyllabusFileId() {
        return syllabusFileId;
    }
    public void setSyllabusFileId(Long syllabusFileId) {
        this.syllabusFileId = syllabusFileId;
    }
    public LocalDateTime getLastScrapedAt() {
        return lastScrapedAt;
    }
    public void setLastScrapedAt(LocalDateTime lastScrapedAt) {
        this.lastScrapedAt = lastScrapedAt;
    }
}
