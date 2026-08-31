package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Semester;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.SemesterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public ResponseEntity<List<Semester>> getSemesters(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(semesterService.getSemesters(currentUser));
    }

    @GetMapping("/active")
    public ResponseEntity<Semester> getActiveSemester(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(semesterService.getActiveSemester(currentUser));
    }

    @PostMapping
    public ResponseEntity<Semester> createSemester(@RequestBody Map<String, String> body, @AuthenticationPrincipal User currentUser) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A semester name is required.");
        }
        Semester created = semesterService.createSemester(name.trim(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Semester> activateSemester(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(semesterService.activateSemester(id, currentUser));
    }
}
