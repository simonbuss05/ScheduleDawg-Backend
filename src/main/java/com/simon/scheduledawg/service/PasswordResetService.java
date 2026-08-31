package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.PasswordResetToken;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.repository.PasswordResetTokenRepository;
import com.simon.scheduledawg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PasswordResetService {

    private static final long TOKEN_EXPIRY_MINUTES = 30;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Always completes normally whether or not the email belongs to an
    // account — the caller must show the same "check your email" message
    // either way, so a bad actor can't use this to discover which emails
    // are registered.
    @Transactional
    public void requestReset(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isEmpty()) return;

        userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
            tokenRepository.deleteAll(tokenRepository.findByUserId(user.getId()));

            String token = generateToken();
            PasswordResetToken resetToken = new PasswordResetToken(
                    user, token, LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES), false
            );
            tokenRepository.save(resetToken);

            String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token == null ? "" : token)
                .orElseThrow(() -> new IllegalArgumentException("This reset link is invalid or has expired."));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This reset link is invalid or has expired.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
