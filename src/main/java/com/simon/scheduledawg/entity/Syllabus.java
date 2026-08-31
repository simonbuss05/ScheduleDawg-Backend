package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "syllabuses")
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String fileName;

    private LocalDateTime uploadedAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] fileData;

    public Syllabus() {

    }
    public Syllabus(Course course, String fileName,  LocalDateTime uploadedAt, byte[] fileData) {
        this.course = course;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.fileData = fileData;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileDate) {
        this.fileData = fileDate;
    }

}
