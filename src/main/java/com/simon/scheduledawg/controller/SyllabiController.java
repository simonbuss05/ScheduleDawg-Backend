package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.SyllabusService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/syllabi")
public class SyllabiController {

    private final SyllabusService syllabusService;

    public SyllabiController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @GetMapping
    public ResponseEntity<List<Syllabus>> getAllSyllabi(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(syllabusService.getAllSyllabuses(currentUser));
    }

    @GetMapping("/{syllabusId}")
    public ResponseEntity<Syllabus> getSyllabus(@PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(syllabusService.getSyllabusById(syllabusId, currentUser));
    }

    @GetMapping("/{syllabusId}/download")
    public ResponseEntity<byte[]> downloadSyllabus(@PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        Syllabus syllabus = syllabusService.getSyllabusById(syllabusId, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + syllabus.getFileName() + "\"")
                .body(syllabus.getFileData());
    }

    @DeleteMapping("/{syllabusId}")
    public ResponseEntity<Void> deleteSyllabus(@PathVariable Long syllabusId, @AuthenticationPrincipal User currentUser) {
        syllabusService.deleteSyllabus(syllabusId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
