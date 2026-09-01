package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.InstructorOption;
import com.simon.scheduledawg.entity.ExternalCourse;
import com.simon.scheduledawg.entity.ExternalCourseInstructor;
import com.simon.scheduledawg.entity.PlannedCourse;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.BulletinScraperService;
import com.simon.scheduledawg.service.PlannedCourseService;
import com.simon.scheduledawg.service.RateLimiterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/plan-ahead/courses")
public class PlannedCourseController {

    private static final long UGA_RMP_SCHOOL_ID = 1101;

    private final PlannedCourseService plannedCourseService;
    private final BulletinScraperService bulletinScraperService;
    private final RateLimiterService rateLimiterService;

    public PlannedCourseController(
            PlannedCourseService plannedCourseService,
            BulletinScraperService bulletinScraperService,
            RateLimiterService rateLimiterService
    ) {
        this.plannedCourseService = plannedCourseService;
        this.bulletinScraperService = bulletinScraperService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    public ResponseEntity<List<PlannedCourse>> getAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(plannedCourseService.getPlannedCourses(currentUser));
    }

    @PostMapping
    public ResponseEntity<PlannedCourse> create(@Valid @RequestBody PlannedCourse plannedCourse, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plannedCourseService.createPlannedCourse(plannedCourse, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        plannedCourseService.deletePlannedCourse(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/instructors")
    public ResponseEntity<List<InstructorOption>> getInstructors(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        PlannedCourse plannedCourse = plannedCourseService.getPlannedCourseById(id, currentUser);

        rateLimiterService.checkOrThrow("plan-ahead-scrape:" + currentUser.getId(), 20, Duration.ofHours(1));

        ExternalCourse externalCourse = bulletinScraperService.findOrScrapeExternalCourse(
                plannedCourse.getSubjectCode(), plannedCourse.getCourseNumber()
        );
        List<ExternalCourseInstructor> instructors = bulletinScraperService.getInstructors(externalCourse);

        List<InstructorOption> options = instructors.stream()
                .map(i -> new InstructorOption(
                        i.getId(),
                        i.getInstructorName(),
                        i.getSyllabusFileId() != null,
                        rmpSearchUrl(i.getInstructorName())
                ))
                .toList();

        return ResponseEntity.ok(options);
    }

    private String rmpSearchUrl(String instructorName) {
        // Names like "LaMarca, Sal (55008, 55029, 55030)" carry a CRN list the
        // bulletin appends to disambiguate sections — strip it before
        // searching RMP, which won't have a professor literally named that.
        String cleanName = instructorName.replaceAll("\\s*\\([^)]*\\)\\s*$", "");
        String encoded = UriUtils.encodeQueryParam(cleanName, StandardCharsets.UTF_8);
        return "https://www.ratemyprofessors.com/search/professors/" + UGA_RMP_SCHOOL_ID + "?q=" + encoded;
    }
}
