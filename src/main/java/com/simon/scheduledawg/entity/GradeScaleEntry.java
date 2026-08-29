package com.simon.scheduledawg.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "grade_scale_entries")
public class GradeScaleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    private String letter;

    private Double minPercent;

    public GradeScaleEntry() {

    }

    public GradeScaleEntry(Course course, String letter, Double minPercent) {
        this.course = course;
        this.letter = letter;
        this.minPercent = minPercent;
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
    public String getLetter() {
        return letter;
    }
    public void setLetter(String letter) {
        this.letter = letter;
    }
    public Double getMinPercent() {
        return minPercent;
    }
    public void setMinPercent(Double minPercent) {
        this.minPercent = minPercent;
    }
}
