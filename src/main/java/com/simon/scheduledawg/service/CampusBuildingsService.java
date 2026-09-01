package com.simon.scheduledawg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.scheduledawg.dto.CampusBuilding;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Fetches UGA campus building locations from OpenStreetMap's Overpass API,
// server-side. The frontend used to call Overpass directly from the browser,
// but Overpass doesn't reliably send CORS headers for arbitrary origins —
// calling it from here sidesteps that entirely, and lets every user share
// one cached result instead of every browser hitting Overpass itself.
@Service
public class CampusBuildingsService {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";
    // South, west, north, east — a few miles around UGA's campus, wider than
    // just campus itself so off-campus locations still match.
    private static final String BBOX = "33.88,-83.45,34.02,-83.28";
    private static final Duration CACHE_TTL = Duration.ofDays(30);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private volatile List<CampusBuilding> cached;
    private volatile Instant cachedAt;

    public CampusBuildingsService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public synchronized List<CampusBuilding> getBuildings() {
        if (cached != null && cachedAt != null && Duration.between(cachedAt, Instant.now()).compareTo(CACHE_TTL) < 0) {
            return cached;
        }

        List<CampusBuilding> fetched = fetchFromOverpass();
        cached = fetched;
        cachedAt = Instant.now();
        return fetched;
    }

    private List<CampusBuilding> fetchFromOverpass() {
        String query = """
                [out:json][timeout:25];
                (
                  way["building"]["name"](%s);
                  node["building"]["name"](%s);
                );
                out center;
                """.formatted(BBOX, BBOX);

        String rawResponse = restClient.post()
                .uri(OVERPASS_URL)
                .body("data=" + query)
                .retrieve()
                .body(String.class);

        List<CampusBuilding> buildings = new ArrayList<>();
        try {
            JsonNode elements = objectMapper.readTree(rawResponse).path("elements");
            for (JsonNode el : elements) {
                JsonNode tags = el.path("tags");
                String name = tags.path("name").asText(null);
                if (name == null) continue;

                double lat = el.has("lat") ? el.path("lat").asDouble() : el.path("center").path("lat").asDouble();
                double lng = el.has("lon") ? el.path("lon").asDouble() : el.path("center").path("lon").asDouble();

                buildings.add(new CampusBuilding(name, lat, lng));
            }
        } catch (Exception e) {
            // If Overpass is unreachable/slow and we have a stale cache, prefer
            // that over an empty list — building autocomplete degrading to
            // "no suggestions" is better than it staying broken for everyone
            // until the next successful fetch.
            if (cached != null) return cached;
            throw new IllegalStateException("Could not load campus building data: " + e.getMessage(), e);
        }

        return buildings;
    }
}
