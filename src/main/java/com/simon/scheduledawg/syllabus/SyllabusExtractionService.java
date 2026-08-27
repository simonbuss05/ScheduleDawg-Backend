package com.simon.scheduledawg.syllabus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class SyllabusExtractionService {

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String MODEL = "claude-sonnet-5";
    private static final String EXTRACTION_PROMPT = """
            You are extracting structured schedule data from a university course syllabus PDF.

            Return ONLY valid JSON matching this exact structure — no markdown code fences, no explanation, no text before or after the JSON:

            {
              "course": {
                "name": string or null,
                "code": string or null,
                "professor": string or null,
                "creditHours": number or null
              },
              "meetings": [
                {
                  "days": array of strings, using only "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY",
                  "startTime": "HH:MM" in 24-hour format,
                  "endTime": "HH:MM" in 24-hour format,
                  "building": string or null,
                  "room": string or null
                }
              ],
              "assignments": [
                { "title": string, "dueDate": "YYYY-MM-DD" }
              ],
              "events": [
                { "title": string, "date": "YYYY-MM-DD" }
              ],
              "finals": [
                { "title": string, "date": "YYYY-MM-DD" or null, "startTime": "HH:MM" or null, "endTime": "HH:MM" or null, "location": string or null }
              ]
            }

            Rules:
            - Group meetings by distinct day/time/location combinations. If lecture and lab meet at different times or locations, create separate meeting entries.
            - Only include an assignment if you find BOTH a specific title AND a specific due date. If a due date says "TBA" or is missing, do not include that assignment at all.
            - Only include an event (quiz, test, exam, midterm, etc.) if you find both a title and a specific date. Do not include events with no date.
            - Finals are different from regular events — extract them separately into the "finals" array, even if some fields (date, time, location) are not stated in the syllabus. Only skip a final entirely if the syllabus never mentions one at all.
            - If you cannot confidently determine a field, use null. Never guess or fabricate a value.
            - Return valid, parseable JSON only.
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    public SyllabusExtractionService(RestClient restClient,ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public SyllabusExtractionResult extractFromPdf(byte[] pdfBytes) {
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "max_tokens", 2048,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "document",
                                                "source", Map.of(
                                                        "type", "base64",
                                                        "media_type", "application/pdf",
                                                        "data", base64Pdf
                                                )
                                        ),
                                        Map.of(
                                                "type", "text",
                                                "text", EXTRACTION_PROMPT
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
            return objectMapper.readValue(cleaned, SyllabusExtractionResult.class);
        } catch (Exception e) {
            throw new SyllabusExtractionException("Could not parse extracted syllabus data.");
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