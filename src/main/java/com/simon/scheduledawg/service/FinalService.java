package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.Final;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.FinalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalService {

    private final FinalRepository finalRepository;
    private final CourseService courseService;

    public FinalService(FinalRepository finalRepository,  CourseService courseService) {
        this.finalRepository = finalRepository;
        this.courseService = courseService;
    }

    public Final createFinal(Final finalEntity, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        finalEntity.setCourse(course);
        return finalRepository.save(finalEntity);
    }

    public List<Final> getFinalByCourseId(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return finalRepository.findByCourseId(course.getId());
    }

    public Final getFinalById(Long courseId, Long finalId) {
        return getFinalScopedToCourse(courseId, finalId);
    }

    private Final getFinalScopedToCourse(Long courseId, Long finalId) {
        Final finalEntity = finalRepository.findById(finalId)
                .orElseThrow(() -> new ResourceNotFoundException("final not found with id: " + finalId));

        if (!finalEntity.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("final not found with id: " + finalId);
        }

        return finalEntity;
    }

    public Final fullyUpdateFinal(Final finalEntity, Long courseId, Long finalId) {
        Final finalEntityToUpdate = getFinalScopedToCourse(courseId, finalId);

        finalEntityToUpdate.setTitle(finalEntity.getTitle());
        finalEntityToUpdate.setDate(finalEntity.getDate());
        finalEntityToUpdate.setStartTime(finalEntity.getStartTime());
        finalEntityToUpdate.setEndTime(finalEntity.getEndTime());
        finalEntityToUpdate.setLocation(finalEntity.getLocation());

        return finalRepository.save(finalEntityToUpdate);
    }

    public void deleteFinalById(Long courseId, Long finalId) {
        Final finalEntity = getFinalScopedToCourse(courseId, finalId);
        finalRepository.delete(finalEntity);
    }

    public void deleteAllFinalsByCourseId(Long courseId) {
        List<Final> finalEntities = finalRepository.findByCourseId(courseId);
        finalRepository.deleteAll(finalEntities);
    }

}
