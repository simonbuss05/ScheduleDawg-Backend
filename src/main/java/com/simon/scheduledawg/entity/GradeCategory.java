package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "grade_categories")
public class GradeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @NotBlank(message = "Category name is required.")
    @Size(max = 255)
    private String name;

    @DecimalMin(value = "0", message = "Weight can't be negative.")
    @DecimalMax(value = "100", message = "Weight can't be more than 100%.")
    private Double weightPercent;

    private Double placeholderScore;

    public GradeCategory() {

    }

    public GradeCategory(Long id,  Course course, String name, Double weightPercent, Double placeholderScore) {
        this.id = id;
        this.course = course;
        this.name = name;
        this.weightPercent = weightPercent;
        this.placeholderScore = placeholderScore;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getWeightPercent() {
        return weightPercent;
    }
    public void setWeightPercent(Double weightPercent) {
        this.weightPercent = weightPercent;
    }
    public Double getPlaceholderScore() {
        return placeholderScore;
    }
    public void setPlaceholderScore(Double placeholderScore) {
        this.placeholderScore = placeholderScore;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
