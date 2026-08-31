package com.simon.scheduledawg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

// Sends transactional email via Resend (https://resend.com). Failures here
// are logged, not thrown — a flaky email provider should never turn into a
// 500 for the user, and forgot-password responses must look identical
// whether or not the underlying email actually went out (see
// PasswordResetService), so there's nothing useful to surface to the caller.
@Service
public class EmailService {

    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final RestClient restClient;

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${mail.from:onboarding@resend.dev}")
    private String fromAddress;

    public EmailService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("RESEND_API_KEY not set — skipping password reset email to " + toEmail);
            return;
        }

        String html = """
                <p>Someone requested a password reset for your ScheduleDawg account.</p>
                <p><a href="%s">Reset your password</a></p>
                <p>This link expires in 30 minutes. If you didn't request this, you can ignore this email.</p>
                """.formatted(resetUrl);

        Map<String, Object> body = Map.of(
                "from", fromAddress,
                "to", List.of(toEmail),
                "subject", "Reset your ScheduleDawg password",
                "html", html
        );

        try {
            restClient.post()
                    .uri(RESEND_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Could not send password reset email: " + e.getMessage());
        }
    }
}
