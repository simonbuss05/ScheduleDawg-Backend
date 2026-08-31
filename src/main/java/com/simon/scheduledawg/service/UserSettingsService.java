package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.entity.UserSettings;
import com.simon.scheduledawg.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettings getSettings(User currentUser) {
        return userSettingsRepository.findByUserId(currentUser.getId()).orElse(new UserSettings());
    }

    public UserSettings updateHomeAddress(User currentUser, String address, Double lat, Double lng) {
        UserSettings settings = userSettingsRepository.findByUserId(currentUser.getId())
                .orElse(new UserSettings());

        settings.setUser(currentUser);
        settings.setHomeAddress(address);
        settings.setHomeLatitude(lat);
        settings.setHomeLongitude(lng);

        return userSettingsRepository.save(settings);
    }

}
