package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
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

    // course itself is @JsonIgnore'd (serializing it dragged in its meetings/
    // assignments/events/semester too), but the frontend still needs to know
    // which course a syllabus belongs to — expose just the identifying
    // fields instead of the whole nested entity.
    @JsonProperty("courseId")
    public Long getCourseId() {
        return course != null ? course.getId() : null;
    }
    @JsonProperty("courseName")
    public String getCourseName() {
        return course != null ? course.getName() : null;
    }
    @JsonProperty("courseCode")
    public String getCourseCode() {
        return course != null ? course.getCode() : null;
    }
}
