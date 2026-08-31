package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.entity.UserSettings;
import com.simon.scheduledawg.service.UserSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettings> getAll(@AuthenticationPrincipal User currentUser) {
        UserSettings userSettings = userSettingsService.getSettings(currentUser);
        return ResponseEntity.ok(userSettings);
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateSettings(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UserSettings userSettings
    ) {
        return ResponseEntity.ok(userSettingsService.updateHomeAddress(
                currentUser, userSettings.getHomeAddress(), userSettings.getHomeLatitude(), userSettings.getHomeLongitude()
        ));
    }

}
