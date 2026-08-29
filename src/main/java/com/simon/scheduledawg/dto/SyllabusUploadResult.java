package com.simon.scheduledawg.dto;

import com.simon.scheduledawg.entity.Syllabus;

public class SyllabusUploadResult {

    private Syllabus syllabus;
    private GradingSchemaExtractionResult grading;

    public SyllabusUploadResult() {

    }

    public SyllabusUploadResult(Syllabus syllabus, GradingSchemaExtractionResult grading) {
        this.syllabus = syllabus;
        this.grading = grading;
    }

    public Syllabus getSyllabus() {
        return syllabus;
    }
    public void setSyllabus(Syllabus syllabus) {
        this.syllabus = syllabus;
    }
    public GradingSchemaExtractionResult getGrading() {
        return grading;
    }
    public void setGrading(GradingSchemaExtractionResult grading) {
        this.grading = grading;
    }
}