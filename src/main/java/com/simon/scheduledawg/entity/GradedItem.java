package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "graded_items")
public class GradedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private GradeCategory category;

    @NotBlank(message = "Title is required.")
    @Size(max = 255)
    private String title;

    @DecimalMin(value = "0", message = "Score can't be negative.")
    private Double percentScore;

    @DecimalMin(value = "0", message = "Points earned can't be negative.")
    private Double pointsEarned;

    @DecimalMin(value = "0", message = "Points possible can't be negative.")
    private Double pointsPossible;

    public GradedItem() {

    }

    public GradedItem(GradeCategory category, String title, Double percentScore, Double pointsEarned, Double pointsPossible) {
        this.category = category;
        this.title = title;
        this.percentScore = percentScore;
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public GradeCategory getCategory() {
        return category;
    }
    public void setCategory(GradeCategory category) {
        this.category = category;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Double getPercentScore() {
        return percentScore;
    }
    public void setPercentScore(Double percentScore) {
        this.percentScore = percentScore;
    }
    public Double getPointsEarned() {
        return pointsEarned;
    }
    public void setPointsEarned(Double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    public Double getPointsPossible() {
        return pointsPossible;
    }
    public void setPointsPossible(Double pointsPossible) {
        this.pointsPossible = pointsPossible;
    }
}