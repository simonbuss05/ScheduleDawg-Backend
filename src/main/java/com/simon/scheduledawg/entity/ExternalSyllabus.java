package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "external_syllabi")
public class ExternalSyllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "external_course_instructor_id")
    @JsonIgnore
    private ExternalCourseInstructor externalCourseInstructor;

    private String fileName;

    private LocalDateTime scrapedAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] fileData;

    public ExternalSyllabus() {

    }

    public ExternalSyllabus(ExternalCourseInstructor externalCourseInstructor, String fileName, LocalDateTime scrapedAt, byte[] fileData) {
        this.externalCourseInstructor = externalCourseInstructor;
        this.fileName = fileName;
        this.scrapedAt = scrapedAt;
        this.fileData = fileData;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ExternalCourseInstructor getExternalCourseInstructor() {
        return externalCourseInstructor;
    }
    public void setExternalCourseInstructor(ExternalCourseInstructor externalCourseInstructor) {
        this.externalCourseInstructor = externalCourseInstructor;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public LocalDateTime getScrapedAt() {
        return scrapedAt;
    }
    public void setScrapedAt(LocalDateTime scrapedAt) {
        this.scrapedAt = scrapedAt;
    }
    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
