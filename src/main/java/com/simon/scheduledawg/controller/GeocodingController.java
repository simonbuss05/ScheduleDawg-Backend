package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.GeocodeResult;
import com.simon.scheduledawg.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocode")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/building")
    public ResponseEntity<GeocodeResult> geocodeBuilding(@RequestParam String query) {
        return ResponseEntity.ok(geocodingService.geocodeBuilding(query));
    }
}
