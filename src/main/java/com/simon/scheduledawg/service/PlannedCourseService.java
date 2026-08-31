package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.PlannedCourse;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.PlannedCourseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlannedCourseService {

    private final PlannedCourseRepository plannedCourseRepository;

    public PlannedCourseService(PlannedCourseRepository plannedCourseRepository) {
        this.plannedCourseRepository = plannedCourseRepository;
    }

    public List<PlannedCourse> getPlannedCourses(User currentUser) {
        return plannedCourseRepository.findByUserId(currentUser.getId());
    }

    public PlannedCourse createPlannedCourse(PlannedCourse plannedCourse, User currentUser) {
        plannedCourse.setUser(currentUser);
        plannedCourse.setCreatedAt(LocalDateTime.now());
        return plannedCourseRepository.save(plannedCourse);
    }

    public PlannedCourse getPlannedCourseById(Long id, User currentUser) {
        PlannedCourse plannedCourse = plannedCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planned course not found with id: " + id));

        if (!plannedCourse.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Planned course not found with id: " + id);
        }

        return plannedCourse;
    }

    public void deletePlannedCourse(Long id, User currentUser) {
        plannedCourseRepository.delete(getPlannedCourseById(id, currentUser));
    }
}
