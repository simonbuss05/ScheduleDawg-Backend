package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.CampusBuilding;
import com.simon.scheduledawg.service.CampusBuildingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/campus-buildings")
public class CampusBuildingsController {

    private final CampusBuildingsService campusBuildingsService;

    public CampusBuildingsController(CampusBuildingsService campusBuildingsService) {
        this.campusBuildingsService = campusBuildingsService;
    }

    @GetMapping
    public ResponseEntity<List<CampusBuilding>> getBuildings() {
        return ResponseEntity.ok(campusBuildingsService.getBuildings());
    }
}
