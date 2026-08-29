package com.simon.scheduledawg.dto;

public class ExtractedGradeScaleEntry {
    private String letter;
    private Double minPercent;

    public ExtractedGradeScaleEntry() {

    }

    public ExtractedGradeScaleEntry(String letter, Double minPercent) {
        this.letter = letter;
        this.minPercent = minPercent;
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
