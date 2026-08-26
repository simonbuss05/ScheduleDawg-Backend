package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.UserSettings;
import com.simon.scheduledawg.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettings getSettings() {
        return userSettingsRepository.findAll().stream().findFirst().orElse(new UserSettings());
    }

    public UserSettings updateHomeAddress(String address, Double lat, Double lng) {
        UserSettings settings = userSettingsRepository.findAll().stream()
                .findFirst()
                .orElse(new UserSettings());

        settings.setHomeAddress(address);
        settings.setHomeLatitude(lat);
        settings.setHomeLongitude(lng);

        return userSettingsRepository.save(settings);
    }

}
