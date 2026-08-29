package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.GradeScaleEntry;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.GradeScaleEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeScaleEntryService {
    private GradeScaleEntryRepository gradeScaleEntryRepository;
    private CourseService courseService;

    public GradeScaleEntryService(GradeScaleEntryRepository gradeScaleEntryRepository, CourseService courseService) {
        this.gradeScaleEntryRepository = gradeScaleEntryRepository;
        this.courseService = courseService;
    }

    public GradeScaleEntry createGradeScaleEntry(GradeScaleEntry gradeScaleEntry, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        gradeScaleEntry.setCourse(course);
        return gradeScaleEntryRepository.save(gradeScaleEntry);
    }

    public List<GradeScaleEntry> getScalesByCourse(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return gradeScaleEntryRepository.findByCourseId(courseId);
    }

    public GradeScaleEntry getGradeScaleEntryById(Long courseId, Long gradeScaleEntryId) {
        return getScaleScopedToCourse(courseId, gradeScaleEntryId);
    }

    private GradeScaleEntry getScaleScopedToCourse(Long courseId, Long gradeScaleEntryId) {
        GradeScaleEntry gradeScaleEntry = gradeScaleEntryRepository.findById(gradeScaleEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade Scale Entry not found with id: " + gradeScaleEntryId));

        if (!gradeScaleEntry.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Grade Scale Entry not found with id: " + gradeScaleEntryId);
        }

        return gradeScaleEntry;
    }

    public GradeScaleEntry fullyUpdateGradeScaleEntry(Long courseId, Long gradeScaleEntryId, GradeScaleEntry gradeScaleEntry) {
        GradeScaleEntry gradeScaleEntryToUpdate = getScaleScopedToCourse(courseId, gradeScaleEntryId);
        gradeScaleEntryToUpdate.setLetter(gradeScaleEntry.getLetter());
        gradeScaleEntryToUpdate.setMinPercent(gradeScaleEntry.getMinPercent());
        return gradeScaleEntryRepository.save(gradeScaleEntryToUpdate);
    }
    public void deleteGradeScaleEntry(Long courseId, Long gradeScaleEntryId) {
        GradeScaleEntry gradeScaleEntry =  getScaleScopedToCourse(courseId, gradeScaleEntryId);
        gradeScaleEntryRepository.delete(gradeScaleEntry);
    }

    public void deleteAllEntriesByCourse(Long courseId) {
        List<GradeScaleEntry> gradeScaleEntries = getScalesByCourse(courseId);
        gradeScaleEntryRepository.deleteAll(gradeScaleEntries);
    }
}

