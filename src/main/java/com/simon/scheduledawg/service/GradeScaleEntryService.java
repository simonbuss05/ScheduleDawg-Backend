package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.GradeScaleEntry;
import com.simon.scheduledawg.entity.User;
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

    public GradeScaleEntry createGradeScaleEntry(GradeScaleEntry gradeScaleEntry, Long courseId, User currentUser) {
        gradeScaleEntry.setCourse(courseService.getCourseById(courseId, currentUser));
        return gradeScaleEntryRepository.save(gradeScaleEntry);
    }

    public List<GradeScaleEntry> getScalesByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        return gradeScaleEntryRepository.findByCourseId(courseId);
    }

    public GradeScaleEntry getGradeScaleEntryById(Long courseId, Long gradeScaleEntryId, User currentUser) {
        return getScaleScopedToCourse(courseId, gradeScaleEntryId, currentUser);
    }

    private GradeScaleEntry getScaleScopedToCourse(Long courseId, Long gradeScaleEntryId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        GradeScaleEntry gradeScaleEntry = gradeScaleEntryRepository.findById(gradeScaleEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade Scale Entry not found with id: " + gradeScaleEntryId));

        if (!gradeScaleEntry.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Grade Scale Entry not found with id: " + gradeScaleEntryId);
        }

        return gradeScaleEntry;
    }

    public GradeScaleEntry fullyUpdateGradeScaleEntry(Long courseId, Long gradeScaleEntryId, GradeScaleEntry gradeScaleEntry, User currentUser) {
        GradeScaleEntry gradeScaleEntryToUpdate = getScaleScopedToCourse(courseId, gradeScaleEntryId, currentUser);
        gradeScaleEntryToUpdate.setLetter(gradeScaleEntry.getLetter());
        gradeScaleEntryToUpdate.setMinPercent(gradeScaleEntry.getMinPercent());
        return gradeScaleEntryRepository.save(gradeScaleEntryToUpdate);
    }
    public void deleteGradeScaleEntry(Long courseId, Long gradeScaleEntryId, User currentUser) {
        GradeScaleEntry gradeScaleEntry =  getScaleScopedToCourse(courseId, gradeScaleEntryId, currentUser);
        gradeScaleEntryRepository.delete(gradeScaleEntry);
    }

    public void deleteAllEntriesByCourse(Long courseId, User currentUser) {
        List<GradeScaleEntry> gradeScaleEntries = getScalesByCourse(courseId, currentUser);
        gradeScaleEntryRepository.deleteAll(gradeScaleEntries);
    }
}
