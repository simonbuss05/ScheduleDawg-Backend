package com.simon.scheduledawg.dto;

import java.util.List;

public class GradingSchemaExtractionResult {

    private List<ExtractedGradeCategory> categories;
    private List<ExtractedGradeScaleEntry> scale;

    public GradingSchemaExtractionResult() {

    }
    public GradingSchemaExtractionResult(List<ExtractedGradeCategory> Categories, List<ExtractedGradeScaleEntry> scale) {
        this.categories = Categories;
        this.scale = scale;
    }
    public List<ExtractedGradeCategory> getCategories() {
        return categories;
    }
    public void setCategories(List<ExtractedGradeCategory> Categories) {
        this.categories = Categories;
    }
    public List<ExtractedGradeScaleEntry> getScale() {
        return scale;
    }
    public void setScale(List<ExtractedGradeScaleEntry> scale) {
        this.scale = scale;
    }


}
