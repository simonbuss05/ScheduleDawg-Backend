package com.simon.scheduledawg.dto;

public class ExtractedGradeCategory {

    private String name;
    private Double weightPercent;

    public ExtractedGradeCategory() {

    }

    public ExtractedGradeCategory(String name, Double weightPercent) {
        this.name = name;
        this.weightPercent = weightPercent;
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
}
