package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Final;
import com.simon.scheduledawg.service.FinalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/finals")
public class FinalController {

    private final FinalService finalService;

    public FinalController(FinalService finalService) {
        this.finalService = finalService;
    }

    @GetMapping
    public ResponseEntity<List<Final>> getFinals(@PathVariable Long courseId) {
        return ResponseEntity.ok(finalService.getFinalByCourseId(courseId));
    }

    @GetMapping("/{finalId}")
    public ResponseEntity<Final> getFinal(@PathVariable Long courseId, @PathVariable Long finalId) {
        return ResponseEntity.ok(finalService.getFinalById(courseId, finalId));
    }

    @PostMapping
    public ResponseEntity<Final> createFinal(@PathVariable Long courseId, @RequestBody Final finalEntity) {
        return ResponseEntity.ok(finalService.createFinal(finalEntity, courseId));
    }

    @PutMapping("/{finalId}")
    public ResponseEntity<Final> updateFinal(@PathVariable Long courseId, @PathVariable Long finalId, @RequestBody Final finalEntity) {
        return ResponseEntity.ok(finalService.fullyUpdateFinal(finalEntity, courseId, finalId));
    }

    @DeleteMapping("/{finalId}")
    public ResponseEntity<Void> deleteFinal(@PathVariable Long courseId, @PathVariable Long finalId) {
        finalService.deleteFinalById(courseId, finalId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllFinals(@PathVariable Long courseId) {
        finalService.deleteAllFinalsByCourseId(courseId);
        return ResponseEntity.noContent().build();
    }
}