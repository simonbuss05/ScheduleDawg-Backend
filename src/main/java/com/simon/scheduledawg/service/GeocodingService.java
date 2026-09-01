package com.simon.scheduledawg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.scheduledawg.dto.GeocodeResult;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Fallback building geocoder for when Mapbox's own search comes up empty —
// proxied through the backend for the same reason CampusBuildingsService
// proxies Overpass: Nominatim's usage policy requires a descriptive
// User-Agent (a plain browser fetch can't set one) and rate-limits at
// roughly 1 req/sec per client, which a shared public instance being hit
// directly from many different users' browsers can trip.
@Service
public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "ScheduleDawg/1.0 (+https://github.com/simonbuss05/ScheduleDawg-Backend)";
    // South-west, north-east corner of the Athens/UGA area — same box the
    // frontend used to pass directly to Nominatim.
    private static final String VIEWBOX = "-83.45,33.87,-83.28,34.02";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeocodingService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public GeocodeResult geocodeBuilding(String query) {
        // The viewbox+bounded params below already restrict results to the
        // Athens/UGA area — appending ", University of Georgia, Athens, GA"
        // to the query text as well (as this used to do) confuses
        // Nominatim's free-text matching and made it return nothing for
        // buildings that resolve fine on their own (confirmed directly
        // against the live API: "Boyd, University of Georgia, Athens, GA"
        // → no results, "Boyd" → Boyd Graduate Research Center).
        String url = NOMINATIM_URL
                + "?format=json&limit=1&bounded=1&viewbox=" + VIEWBOX
                + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        String rawResponse;
        try {
            rawResponse = restClient.get()
                    .uri(url)
                    .header("User-Agent", USER_AGENT)
                    .header("Accept-Language", "en")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Could not locate \"" + query + "\".");
        }

        try {
            JsonNode results = objectMapper.readTree(rawResponse);
            if (!results.isArray() || results.isEmpty()) {
                throw new ResourceNotFoundException("Could not locate \"" + query + "\".");
            }
            JsonNode match = results.get(0);
            double lat = Double.parseDouble(match.path("lat").asText());
            double lng = Double.parseDouble(match.path("lon").asText());
            String placeName = match.path("display_name").asText(query);
            return new GeocodeResult(lat, lng, placeName);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Could not locate \"" + query + "\".");
        }
    }
}
