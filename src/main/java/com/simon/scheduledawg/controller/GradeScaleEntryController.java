package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.GradeScaleEntry;
import com.simon.scheduledawg.service.GradeScaleEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/scale")
public class GradeScaleEntryController {

    private GradeScaleEntryService gradeScaleEntryService;

    public  GradeScaleEntryController(GradeScaleEntryService gradeScaleEntryService) {
        this.gradeScaleEntryService = gradeScaleEntryService;
    }

    @GetMapping
    public ResponseEntity<List<GradeScaleEntry>> getScales(@PathVariable Long courseId) {
       return ResponseEntity.ok(gradeScaleEntryService.getScalesByCourse(courseId));
    }

    @GetMapping("/{scaleId}")
    public ResponseEntity<GradeScaleEntry> getScale(@PathVariable Long scaleId, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeScaleEntryService.getGradeScaleEntryById(courseId, scaleId));
    }

    @PostMapping
    public ResponseEntity<GradeScaleEntry> createScale(@RequestBody GradeScaleEntry gradeScaleEntry, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeScaleEntryService.createGradeScaleEntry(gradeScaleEntry, courseId));
    }

    @PutMapping("/{scaleId}")
    public ResponseEntity<GradeScaleEntry> fullyUpdateScale(@PathVariable Long courseId, @PathVariable Long scaleId, @RequestBody GradeScaleEntry gradeScaleEntry) {
        return ResponseEntity.ok(gradeScaleEntryService.fullyUpdateGradeScaleEntry(courseId, scaleId, gradeScaleEntry));
    }

    @DeleteMapping("/{scaleId}")
    public ResponseEntity<Void> deleteScale(@PathVariable Long scaleId, @PathVariable Long courseId) {
        gradeScaleEntryService.deleteGradeScaleEntry(courseId, scaleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllScalesByCourse(@PathVariable Long courseId) {
        gradeScaleEntryService.deleteAllEntriesByCourse(courseId);
        return ResponseEntity.noContent().build();
    }




}
