package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Assignment;
import com.simon.scheduledawg.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAll(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourseId(courseId));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<Assignment> getAssignment(@PathVariable Long assignmentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(courseId, assignmentId));
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@PathVariable Long courseId, @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.createAssignment(assignment, courseId));
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<Assignment> fullyUpdateAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId, @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.fullyUpdateAssignment(assignment,courseId, assignmentId));
    }

    @PatchMapping("/{assignmentId}")
    public ResponseEntity<Assignment> partialUpdateAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId, @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.partialUpdateAssignment(assignment,courseId, assignmentId));
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        assignmentService.deleteAssignment(courseId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAssignments(@PathVariable Long courseId) {
        assignmentService.deleteAllAssignmentsByCourseId(courseId);
        return ResponseEntity.noContent().build();
    }



}
