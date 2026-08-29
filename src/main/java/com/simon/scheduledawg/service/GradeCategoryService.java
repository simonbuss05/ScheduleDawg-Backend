package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.GradeCategory;
import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.GradeCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeCategoryService {

    private GradeCategoryRepository gradeCategoryRepository;
    private CourseService courseService;

    public GradeCategoryService(GradeCategoryRepository gradeCategoryRepository, CourseService courseService) {
        this.gradeCategoryRepository = gradeCategoryRepository;
        this.courseService = courseService;
    }

    public GradeCategory createCategory(GradeCategory gradeCategory, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        gradeCategory.setCourse(course);
        return gradeCategoryRepository.save(gradeCategory);
    }

    public List<GradeCategory> getCategoriesByCourse(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return gradeCategoryRepository.findByCourseId(courseId);
    }

    public GradeCategory findById(Long courseId, Long categoryId) {
        return getCategoryScopedToCourse(courseId, categoryId);
    }

    private GradeCategory getCategoryScopedToCourse(Long courseId, Long categoryId) {
        GradeCategory gradeCategory = gradeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!gradeCategory.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        return gradeCategory;
    }

    public GradeCategory fullyUpdateCategory(GradeCategory gradeCategory, Long courseId, Long categoryId) {
        GradeCategory gradeCategoryToUpdate = getCategoryScopedToCourse(courseId, categoryId);
        gradeCategoryToUpdate.setName(gradeCategory.getName());
        gradeCategoryToUpdate.setPlaceholderScore(gradeCategory.getPlaceholderScore());
        gradeCategoryToUpdate.setWeightPercent(gradeCategory.getWeightPercent());
        return gradeCategoryRepository.save(gradeCategoryToUpdate);
    }

    public GradeCategory partialUpdateCategory(GradeCategory gradeCategory, Long courseId, Long categoryId) {
        GradeCategory gradeCategoryToUpdate = getCategoryScopedToCourse(courseId, categoryId);
        if (gradeCategory.getPlaceholderScore() == null) {
            gradeCategoryToUpdate.setPlaceholderScore(gradeCategory.getPlaceholderScore());
        }
        if (gradeCategory.getWeightPercent() == null) {
            gradeCategoryToUpdate.setWeightPercent(gradeCategory.getWeightPercent());
        }
        if (gradeCategory.getName() == null) {
            gradeCategoryToUpdate.setName(gradeCategory.getName());
        }

        return gradeCategoryRepository.save(gradeCategoryToUpdate);
    }

    public void deleteCategory(Long courseId, Long categoryId) {
        GradeCategory gradeCategoryToDelete = getCategoryScopedToCourse(courseId, categoryId);
        gradeCategoryRepository.delete(gradeCategoryToDelete);
    }

    public void deleteAllCategoriesByCourse(Long courseId) {
        List<GradeCategory> gradeCategories = gradeCategoryRepository.findByCourseId(courseId);
        gradeCategoryRepository.deleteAll(gradeCategories);
    }

}
