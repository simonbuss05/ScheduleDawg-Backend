package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.GradingSchemaExtractionResult;
import com.simon.scheduledawg.entity.ExternalCourseInstructor;
import com.simon.scheduledawg.entity.ExternalSyllabus;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.BulletinScraperService;
import com.simon.scheduledawg.service.RateLimiterService;
import com.simon.scheduledawg.service.SyllabusExtractionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/plan-ahead/instructors")
public class ExternalInstructorController {

    private final BulletinScraperService bulletinScraperService;
    private final SyllabusExtractionService syllabusExtractionService;
    private final RateLimiterService rateLimiterService;

    public ExternalInstructorController(
            BulletinScraperService bulletinScraperService,
            SyllabusExtractionService syllabusExtractionService,
            RateLimiterService rateLimiterService
    ) {
        this.bulletinScraperService = bulletinScraperService;
        this.syllabusExtractionService = syllabusExtractionService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/{id}/syllabus")
    public ResponseEntity<byte[]> getSyllabus(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        rateLimiterService.checkOrThrow("plan-ahead-scrape:" + currentUser.getId(), 20, Duration.ofHours(1));

        ExternalCourseInstructor instructor = bulletinScraperService.getInstructorById(id);
        ExternalSyllabus syllabus = bulletinScraperService.getOrDownloadSyllabus(instructor);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + syllabus.getFileName() + "\"")
                .body(syllabus.getFileData());
    }

    @GetMapping("/{id}/grading-schema")
    public ResponseEntity<GradingSchemaExtractionResult> getGradingSchema(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        rateLimiterService.checkOrThrow("plan-ahead-scrape:" + currentUser.getId(), 20, Duration.ofHours(1));

        ExternalCourseInstructor instructor = bulletinScraperService.getInstructorById(id);
        ExternalSyllabus syllabus = bulletinScraperService.getOrDownloadSyllabus(instructor);

        return ResponseEntity.ok(syllabusExtractionService.extractGradingSchema(syllabus.getFileData()));
    }
}
