package com.simon.scheduledawg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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

    private String title;

    private Double percentScore;

    private Double pointsEarned;

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