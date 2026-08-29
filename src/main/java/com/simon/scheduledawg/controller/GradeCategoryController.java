package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.GradeCategory;
import com.simon.scheduledawg.service.GradeCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/gradeCategories")
public class GradeCategoryController {

    private final GradeCategoryService gradeCategoryService;

    public GradeCategoryController(GradeCategoryService gradeCategoryService) {
        this.gradeCategoryService = gradeCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<GradeCategory>> getCategories(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCategoryService.getCategoriesByCourse(courseId));
    }

    @GetMapping("/{gradeCategoryId}")
    public ResponseEntity<GradeCategory> getCategory(@PathVariable Long gradeCategoryId, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCategoryService.findById(courseId, gradeCategoryId));
    }

    @PostMapping
    public ResponseEntity<GradeCategory> createCategory(@RequestBody GradeCategory gradeCategory, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCategoryService.createCategory(gradeCategory, courseId));
    }

    @PutMapping("/{gradeCategoryId}")
    public ResponseEntity<GradeCategory> fullyUpdateCategory(@PathVariable Long gradeCategoryId, @RequestBody GradeCategory gradeCategory, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCategoryService.fullyUpdateCategory(gradeCategory, courseId, gradeCategoryId));
    }

    @PatchMapping("/{gradeCategoryId}")
    public ResponseEntity<GradeCategory> partialUpdateCategory(@PathVariable Long gradeCategoryId, @RequestBody GradeCategory gradeCategory, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCategoryService.partialUpdateCategory(gradeCategory, courseId, gradeCategoryId));
    }

    @DeleteMapping("/{gradeCategoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long gradeCategoryId, @PathVariable Long courseId) {
        gradeCategoryService.deleteCategory(courseId, gradeCategoryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<GradeCategory> deleteAllByCategory(@PathVariable Long courseId) {
        gradeCategoryService.deleteAllCategoriesByCourse(courseId);
        return ResponseEntity.noContent().build();
    }

}
