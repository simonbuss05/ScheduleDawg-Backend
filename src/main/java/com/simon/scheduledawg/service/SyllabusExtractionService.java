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
import java.util.List;
import java.util.Map;

@Service
public class SyllabusExtractionService {

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String MODEL = "claude-sonnet-5";

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
            - "categories" should list every grading component and its weight, e.g. "Homework: 20%", "Midterm Exam: 25%". Weights should sum to approximately 100.
            - "scale" should list every letter grade cutoff mentioned (including +/- grades like A-, B+, etc.), with the MINIMUM percent required for that letter. For a range like "90 - 92.9 = A-", use minPercent 90. For "93 and above = A", use minPercent 93.
            - Order the scale from highest letter grade to lowest.
            - If either the grading policy or grading scale cannot be found in this document, return an empty array for that field — do not guess or fabricate values.
            - Return valid, parseable JSON only.
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    public SyllabusExtractionService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public GradingSchemaExtractionResult extractGradingSchema(byte[] pdfBytes) {
        String syllabusText = extractTextFromPdf(pdfBytes);

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

        try {
            return objectMapper.readValue(cleaned, GradingSchemaExtractionResult.class);
        } catch (Exception e) {
            throw new SyllabusExtractionException("Could not parse extracted grading data.");
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