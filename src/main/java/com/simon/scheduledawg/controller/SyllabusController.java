package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.GradingSchemaExtractionResult;
import com.simon.scheduledawg.dto.SyllabusUploadResult;
import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.SyllabusExtractionException;
import com.simon.scheduledawg.service.SyllabusExtractionService;
import com.simon.scheduledawg.service.SyllabusService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/syllabi")
public class SyllabusController {

    private final SyllabusService syllabusService;
    private final SyllabusExtractionService syllabusExtractionService;

    public SyllabusController(SyllabusService syllabusService, SyllabusExtractionService syllabusExtractionService) {
        this.syllabusService = syllabusService;
        this.syllabusExtractionService = syllabusExtractionService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Syllabus>> getSyllabiByCourse(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(syllabusService.getSyllabiByCourse(courseId, currentUser));
    }

    @GetMapping("/{syllabusId}")
    public ResponseEntity<Syllabus> getSyllabus(@PathVariable Long courseId, @PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(syllabusService.findSyllabusById(courseId, syllabusId, currentUser));
    }

    @GetMapping("/{syllabusId}/download")
    public ResponseEntity<byte[]> downloadSyllabus(@PathVariable Long courseId, @PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        Syllabus syllabus = syllabusService.findSyllabusById(courseId, syllabusId, currentUser);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + syllabus.getFileName() + "\"")
                .body(syllabus.getFileData());
    }

    @PostMapping
    public ResponseEntity<?> uploadSyllabus(@PathVariable Long courseId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User currentUser) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No file uploaded."));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF files are supported."));
        }

        byte[] pdfBytes;
        try {
            pdfBytes = file.getBytes();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Could not read uploaded file."));
        }

        Syllabus savedSyllabus = syllabusService.createSyllabus(pdfBytes, file.getOriginalFilename(), courseId, currentUser);

        GradingSchemaExtractionResult grading;
        try {
            grading = syllabusExtractionService.extractGradingSchema(pdfBytes);
        } catch (SyllabusExtractionException e) {
            grading = new GradingSchemaExtractionResult();
        }

        return ResponseEntity.ok(new SyllabusUploadResult(savedSyllabus, grading));
    }

    @DeleteMapping("/{syllabusId}")
    public ResponseEntity<Void> deleteSyllabus(@PathVariable Long courseId, @PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        syllabusService.deleteSyllabus(courseId, syllabusId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSyllabi(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        syllabusService.deleteAllSyllabusesByCourse(courseId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
