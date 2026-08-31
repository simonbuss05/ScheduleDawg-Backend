package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.dto.AuthRequest;
import com.simon.scheduledawg.dto.AuthResponse;
import com.simon.scheduledawg.dto.ChangePasswordRequest;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.AuthService;
import com.simon.scheduledawg.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimiterService rateLimiterService;

    public AuthController(AuthService authService, RateLimiterService rateLimiterService) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        rateLimiterService.checkOrThrow("register-ip:" + clientIp(httpRequest), 5, Duration.ofHours(1));
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        // Two limits: a looser one per IP (so one attacker can't lock out an
        // entire shared IP, e.g. a dorm's NAT) and a tighter one per email
        // (so a single account can't be brute-forced from many IPs).
        rateLimiterService.checkOrThrow("login-ip:" + clientIp(httpRequest), 20, Duration.ofMinutes(1));
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            rateLimiterService.checkOrThrow("login-email:" + request.getEmail().trim().toLowerCase(), 5, Duration.ofMinutes(1));
        }
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(currentUser, request);
        return ResponseEntity.noContent().build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
