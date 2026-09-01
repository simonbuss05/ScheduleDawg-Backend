package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.GradingSchemaExtractionResult;
import com.simon.scheduledawg.dto.SyllabusUploadResult;
import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.SyllabusExtractionException;
import com.simon.scheduledawg.service.RateLimiterService;
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
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/syllabi")
public class SyllabusController {

    private final SyllabusService syllabusService;
    private final SyllabusExtractionService syllabusExtractionService;
    private final RateLimiterService rateLimiterService;

    public SyllabusController(
            SyllabusService syllabusService,
            SyllabusExtractionService syllabusExtractionService,
            RateLimiterService rateLimiterService
    ) {
        this.syllabusService = syllabusService;
        this.syllabusExtractionService = syllabusExtractionService;
        this.rateLimiterService = rateLimiterService;
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
                // Without this, a browser that decides to sniff the actual
                // bytes rather than trust the declared Content-Type could
                // render a maliciously-mislabeled upload as HTML instead of
                // a PDF.
                .header("X-Content-Type-Options", "nosniff")
                .body(syllabus.getFileData());
    }

    @PostMapping
    public ResponseEntity<?> uploadSyllabus(@PathVariable Long courseId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User currentUser) {
        // Each upload can trigger a paid Claude API call, so cap how often one
        // account can hit this endpoint regardless of how many courses they have.
        rateLimiterService.checkOrThrow("syllabus-upload:" + currentUser.getId(), 10, Duration.ofHours(1));

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

        // The declared Content-Type above is just a client-supplied form
        // field — nothing stops a request from labeling any file "PDF"
        // regardless of its actual bytes. Checking the real PDF magic
        // number means what we store and later serve back (as an "inline"
        // PDF, with a forced Content-Type) is actually a PDF.
        if (!isPdf(pdfBytes)) {
            return ResponseEntity.badRequest().body(Map.of("message", "That file isn't a valid PDF."));
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

    private static boolean isPdf(byte[] bytes) {
        byte[] magic = {'%', 'P', 'D', 'F', '-'};
        if (bytes.length < magic.length) return false;
        for (int i = 0; i < magic.length; i++) {
            if (bytes[i] != magic[i]) return false;
        }
        return true;
    }
}
