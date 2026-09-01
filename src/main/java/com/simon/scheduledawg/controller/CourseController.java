package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) Long semesterId,
            @AuthenticationPrincipal User currentUser
    ){
        return ResponseEntity.ok(courseService.getAllCourses(semesterId, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id, @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(courseService.getCourseById(id, currentUser));
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course, @AuthenticationPrincipal User currentUser){
        Course saved = courseService.createCourse(course, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course course, @AuthenticationPrincipal User currentUser){
        Course updated = courseService.updateFullCourse(id, course, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, @AuthenticationPrincipal User currentUser){
        courseService.deleteCourse(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCourses(@AuthenticationPrincipal User currentUser){
        courseService.deleteAllCourses(currentUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Course> patchCourse(@PathVariable Long id, @RequestBody Course course, @AuthenticationPrincipal User currentUser){
        Course updated = courseService.updatePartialCourse(id, course, currentUser);
        return ResponseEntity.ok(updated);
    }

}
