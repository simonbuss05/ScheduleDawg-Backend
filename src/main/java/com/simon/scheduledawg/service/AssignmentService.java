package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Assignment;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseService courseService;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseService courseService) {
        this.assignmentRepository = assignmentRepository;
        this.courseService = courseService;
    }

    public Assignment createAssignment(Assignment assignment, Long courseId, User currentUser){
        assignment.setCourse(courseService.getCourseById(courseId, currentUser));
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsByCourseId(Long courseId, User currentUser){
        courseService.getCourseById(courseId, currentUser);
        return assignmentRepository.findByCourseId(courseId);
    }

    public Assignment getAssignmentById(Long courseId, Long assignmentId, User currentUser){
        return getAssignmentScopedToCourse(courseId, assignmentId, currentUser);
    }

    private Assignment getAssignmentScopedToCourse(Long courseId, Long assignmentId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        if (!assignment.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }

        return assignment;
    }

    public Assignment fullyUpdateAssignment(Assignment assignment, Long courseId, Long assignmentId, User currentUser) {
        Assignment assignmentToUpdate = getAssignmentScopedToCourse(courseId, assignmentId, currentUser);
        assignmentToUpdate.setTitle(assignment.getTitle());
        assignmentToUpdate.setDescription(assignment.getDescription());
        assignmentToUpdate.setCompleted(assignment.isCompleted());
        assignmentToUpdate.setDueDate(assignment.getDueDate());
        assignmentToUpdate.setDueTime(assignment.getDueTime());
        return assignmentRepository.save(assignmentToUpdate);
    }

    public Assignment partialUpdateAssignment(Assignment assignment, Long courseId, Long assignmentId, User currentUser) {
        Assignment assignmentToUpdate = getAssignmentScopedToCourse(courseId, assignmentId, currentUser);
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

    public void deleteAssignment(Long courseId, Long assignmentId, User currentUser) {
        Assignment assignment = getAssignmentScopedToCourse(courseId, assignmentId, currentUser);
        assignmentRepository.delete(assignment);
    }

    public void deleteAllAssignmentsByCourseId(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        assignmentRepository.deleteAll(assignmentRepository.findByCourseId(courseId));
    }
}
