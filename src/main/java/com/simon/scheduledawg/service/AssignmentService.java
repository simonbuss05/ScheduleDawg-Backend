package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Assignment;
import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.AssignmentRepository;
import com.simon.scheduledawg.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, CourseService courseService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    public Assignment createAssignment(Assignment assignment, Long courseId){
        Course course = courseService.getCourseById(courseId);
        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsByCourseId(Long courseId){
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return assignmentRepository.findByCourseId(courseId);
    }

    public Assignment getAssignmentById(Long courseId, Long assignmentId){
        return getAssignmentScopedToCourse(courseId, assignmentId);
    }

    private Assignment getAssignmentScopedToCourse(Long courseId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        if (!assignment.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }

        return assignment;
    }

    public Assignment fullyUpdateAssignment(Assignment assignment, Long courseId, Long assignmentId) {
        Assignment assignmentToUpdate = getAssignmentScopedToCourse(courseId, assignmentId);
        assignmentToUpdate.setTitle(assignment.getTitle());
        assignmentToUpdate.setDescription(assignment.getDescription());
        assignmentToUpdate.setCompleted(assignment.isCompleted());
        assignmentToUpdate.setDueDate(assignment.getDueDate());
        assignmentToUpdate.setDueTime(assignment.getDueTime());
        return assignmentRepository.save(assignmentToUpdate);
    }

    public Assignment partialUpdateAssignment(Assignment assignment, Long courseId, Long assignmentId) {
        Assignment assignmentToUpdate = getAssignmentScopedToCourse(courseId, assignmentId);
        if (assignment.getTitle() != null) {
            assignmentToUpdate.setTitle(assignment.getTitle());
        }
        if (assignment.getDescription() != null) {
            assignmentToUpdate.setDescription(assignment.getDescription());
        }
        if (assignment.getDueDate() != null) {
            assignmentToUpdate.setDueDate(assignment.getDueDate());
        }
        if (assignment.getDueTime() != null) {
            assignmentToUpdate.setDueTime(assignment.getDueTime());
        }
        if (assignment.isCompleted() != null) {
            assignmentToUpdate.setCompleted(assignment.isCompleted());
        }
        return assignmentRepository.save(assignmentToUpdate);
    }

    public void deleteAssignment(Long courseId, Long assignmentId) {
        Assignment assignment = getAssignmentScopedToCourse(courseId, assignmentId);
        assignmentRepository.delete(assignment);
    }

    public void deleteAllAssignmentsByCourseId(Long courseId) {
        assignmentRepository.deleteAll(assignmentRepository.findByCourseId(courseId));
    }
}