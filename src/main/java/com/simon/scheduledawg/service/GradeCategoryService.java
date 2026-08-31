package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.GradeCategory;
import com.simon.scheduledawg.entity.User;
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

    public GradeCategory createCategory(GradeCategory gradeCategory, Long courseId, User currentUser) {
        gradeCategory.setCourse(courseService.getCourseById(courseId, currentUser));
        return gradeCategoryRepository.save(gradeCategory);
    }

    public List<GradeCategory> getCategoriesByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        return gradeCategoryRepository.findByCourseId(courseId);
    }

    public GradeCategory findById(Long courseId, Long categoryId, User currentUser) {
        return getCategoryScopedToCourse(courseId, categoryId, currentUser);
    }

    private GradeCategory getCategoryScopedToCourse(Long courseId, Long categoryId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        GradeCategory gradeCategory = gradeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!gradeCategory.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        return gradeCategory;
    }

    public GradeCategory fullyUpdateCategory(GradeCategory gradeCategory, Long courseId, Long categoryId, User currentUser) {
        GradeCategory gradeCategoryToUpdate = getCategoryScopedToCourse(courseId, categoryId, currentUser);
        gradeCategoryToUpdate.setName(gradeCategory.getName());
        gradeCategoryToUpdate.setPlaceholderScore(gradeCategory.getPlaceholderScore());
        gradeCategoryToUpdate.setWeightPercent(gradeCategory.getWeightPercent());
        return gradeCategoryRepository.save(gradeCategoryToUpdate);
    }

    public GradeCategory partialUpdateCategory(GradeCategory gradeCategory, Long courseId, Long categoryId, User currentUser) {
        GradeCategory gradeCategoryToUpdate = getCategoryScopedToCourse(courseId, categoryId, currentUser);
        if (gradeCategory.getPlaceholderScore() != null) {
            gradeCategoryToUpdate.setPlaceholderScore(gradeCategory.getPlaceholderScore());
        }
        if (gradeCategory.getWeightPercent() != null) {
            gradeCategoryToUpdate.setWeightPercent(gradeCategory.getWeightPercent());
        }
        if (gradeCategory.getName() != null) {
            gradeCategoryToUpdate.setName(gradeCategory.getName());
        }

        return gradeCategoryRepository.save(gradeCategoryToUpdate);
    }

    public void deleteCategory(Long courseId, Long categoryId, User currentUser) {
        GradeCategory gradeCategoryToDelete = getCategoryScopedToCourse(courseId, categoryId, currentUser);
        gradeCategoryRepository.delete(gradeCategoryToDelete);
    }

    public void deleteAllCategoriesByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        List<GradeCategory> gradeCategories = gradeCategoryRepository.findByCourseId(courseId);
        gradeCategoryRepository.deleteAll(gradeCategories);
    }

}
