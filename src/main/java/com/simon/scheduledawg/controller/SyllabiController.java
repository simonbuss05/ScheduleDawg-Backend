package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.service.SyllabusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/syllabi")
public class SyllabiController {

    private final SyllabusService syllabusService;

    public SyllabiController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @GetMapping
    public ResponseEntity<List<Syllabus>> getAllSyllabi() {
        return ResponseEntity.ok(syllabusService.getAllSyllabuses());
    }
}