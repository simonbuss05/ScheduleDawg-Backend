package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.UserSettings;
import com.simon.scheduledawg.service.UserSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettings> getAll() {
        UserSettings userSettings = userSettingsService.getSettings();
        return ResponseEntity.ok(userSettings);
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateSettings(@RequestBody UserSettings userSettings) {
        return ResponseEntity.ok(userSettingsService.updateHomeAddress(userSettings.getHomeAddress(), userSettings.getHomeLatitude(), userSettings.getHomeLongitude()));
    }


}
