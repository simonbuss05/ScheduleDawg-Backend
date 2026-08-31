package com.simon.scheduledawg.service;

import com.simon.scheduledawg.dto.GradingSchemaExtractionResult;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Caches extraction results by a hash of the PDF's content, so re-uploading
 * the exact same file (a mis-click, a retry, two students on the same
 * syllabus) reuses the previous Claude call instead of paying for it again.
 * In-memory and bounded — reset on restart, which is fine for this use.
 */
@Component
public class SyllabusExtractionCache {

    private static final int MAX_ENTRIES = 200;

    private final Map<String, GradingSchemaExtractionResult> cache =
            new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, GradingSchemaExtractionResult> eldest) {
                    return size() > MAX_ENTRIES;
                }
            };

    public synchronized GradingSchemaExtractionResult get(String contentHash) {
        return cache.get(contentHash);
    }

    public synchronized void put(String contentHash, GradingSchemaExtractionResult result) {
        cache.put(contentHash, result);
    }
}
