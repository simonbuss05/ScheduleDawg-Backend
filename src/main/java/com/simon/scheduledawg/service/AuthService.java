package com.simon.scheduledawg.service;

import com.simon.scheduledawg.dto.AuthRequest;
import com.simon.scheduledawg.dto.AuthResponse;
import com.simon.scheduledawg.dto.ChangePasswordRequest;
import com.simon.scheduledawg.entity.Semester;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.repository.CourseRepository;
import com.simon.scheduledawg.repository.UserRepository;
import com.simon.scheduledawg.repository.UserSettingsRepository;
import com.simon.scheduledawg.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final SemesterService semesterService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            UserSettingsRepository userSettingsRepository,
            SemesterService semesterService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.semesterService = semesterService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String email = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();
        if (email == null || email.isEmpty() || request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Email and a password of at least 8 characters are required.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }

        final User savedUser = userRepository.save(new User(email, passwordEncoder.encode(request.getPassword())));

        // Every account needs an active semester to create courses into.
        Semester defaultSemester = semesterService.createSemester(SemesterService.defaultSemesterName(), savedUser);

        // The app started single-tenant with no user concept at all. The very first
        // account ever created inherits any pre-existing data (courses, settings)
        // that isn't already attached to a user, rather than requiring a manual
        // migration step. Those courses land in the new default semester too, so
        // they aren't silently hidden once course listing filters by semester.
        if (userRepository.count() == 1) {
            courseRepository.findAll().forEach(course -> {
                if (course.getUser() == null) {
                    course.setUser(savedUser);
                    course.setSemester(defaultSemester);
                    courseRepository.save(course);
                }
            });
            userSettingsRepository.findAll().forEach(settings -> {
                if (settings.getUser() == null) {
                    settings.setUser(savedUser);
                    userSettingsRepository.save(settings);
                }
            });
        }

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail());
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email == null ? "" : email)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect email or password."));

        if (request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect email or password.");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (request.getCurrentPassword() == null
                || !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
