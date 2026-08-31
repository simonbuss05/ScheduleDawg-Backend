package com.simon.scheduledawg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.scheduledawg.dto.GradingSchemaExtractionResult;
import com.simon.scheduledawg.exception.SyllabusExtractionException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class SyllabusExtractionService {

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String MODEL = "claude-haiku-4-5-20251001";

    // The grading policy is almost always near the front of a syllabus; capping
    // the text we send keeps token cost (and therefore $) bounded even for
    // unusually long documents (multi-week schedules, appended readings, etc.).
    private static final int MAX_SYLLABUS_TEXT_CHARS = 20_000;

    private static final String EXTRACTION_PROMPT = """
        You are extracting the grading policy from a university course syllabus.

        Return ONLY valid JSON matching this exact structure — no markdown code fences, no explanation, no text before or after the JSON:

        {
          "categories": [
            { "name": string, "weightPercent": number }
          ],
          "scale": [
            { "letter": string, "minPercent": number }
          ]
        }

        Rules:
        - "categories" should list every grading component and its weight as a PERCENT (weightPercent), e.g. "Homework: 20%", "Midterm Exam: 25%". Weights should sum to approximately 100.
        - Some syllabi express category weights in raw POINTS instead of percentages (e.g. "Homework: 140 pts", "Final Exam: 50 pts"). If you see this, convert each category to a percent yourself: weightPercent = (category's points / total points across all categories) * 100. If the syllabus states an explicit total (e.g. "Total: 288 pts"), use that as the denominator. If no explicit total is stated, sum every category's points yourself to get the total.
        - Do not include a category for the total/sum row itself (e.g. skip a "Total: 288 pts" line) — only include the actual individual grading components.
        - "scale" should list every letter grade cutoff mentioned (including +/- grades like A-, B+, etc.), with the MINIMUM percent required for that letter. For a range like "90 - 92.9 = A-", use minPercent 90. For "93 and above = A", use minPercent 93.
        - Order the scale from highest letter grade to lowest.
        - If either the grading policy or grading scale cannot be found in this document, return an empty array for that field — do not guess or fabricate values.
        - Return valid, parseable JSON only.
        """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SyllabusExtractionCache extractionCache;

    @Value("${anthropic.api.key}")
    private String apiKey;

    public SyllabusExtractionService(RestClient restClient, ObjectMapper objectMapper, SyllabusExtractionCache extractionCache) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.extractionCache = extractionCache;
    }

    public GradingSchemaExtractionResult extractGradingSchema(byte[] pdfBytes) {
        String contentHash = sha256Hex(pdfBytes);
        GradingSchemaExtractionResult cached = extractionCache.get(contentHash);
        if (cached != null) {
            return cached;
        }

        String syllabusText = extractTextFromPdf(pdfBytes);
        if (syllabusText.length() > MAX_SYLLABUS_TEXT_CHARS) {
            syllabusText = syllabusText.substring(0, MAX_SYLLABUS_TEXT_CHARS);
        }

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "text",
                                                "text", "Syllabus text:\n\n" + syllabusText + "\n\n" + EXTRACTION_PROMPT
                                        )
                                )
                        )
                )
        );

        String rawResponse;
        try {
            rawResponse = restClient.post()
                    .uri(ANTHROPIC_URL)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new SyllabusExtractionException("Could not reach Claude API: " + e.getMessage());
        }

        String extractedText;
        try {
            JsonNode envelope = objectMapper.readTree(rawResponse);
            extractedText = envelope.path("content").get(0).path("text").asText();
        } catch (Exception e) {
            throw new SyllabusExtractionException("Unexpected response format from Claude API.");
        }

        String cleaned = cleanJsonText(extractedText);

        GradingSchemaExtractionResult result;
        try {
            result = objectMapper.readValue(cleaned, GradingSchemaExtractionResult.class);
        } catch (Exception e) {
            throw new SyllabusExtractionException("Could not parse extracted grading data.");
        }

        extractionCache.put(contentHash, result);
        return result;
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private String extractTextFromPdf(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new SyllabusExtractionException("Could not read the PDF file.");
        }
    }

    private String cleanJsonText(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline != -1 && lastFence > firstNewline) {
                trimmed = trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}