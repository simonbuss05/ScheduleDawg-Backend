package com.simon.scheduledawg.service;

import com.simon.scheduledawg.exception.RateLimitExceededException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory sliding-window rate limiter, keyed by caller-supplied strings
 * (e.g. "login-ip:1.2.3.4"). Good enough for a single-instance deployment;
 * a multi-instance one would need a shared store (e.g. Redis) instead.
 *
 * Some keys embed client-supplied values directly (e.g. "login-email:" +
 * whatever email was submitted, on a public, unauthenticated endpoint), so
 * an attacker can otherwise grow this map without bound by submitting an
 * endless stream of distinct bogus values — each one permanently allocating
 * an entry that nothing else ever removes. The periodic sweep below evicts
 * any key whose window has fully expired.
 */
@Service
public class RateLimiterService {

    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    public void checkOrThrow(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        long windowStartMs = now - window.toMillis();
        Deque<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStartMs) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                throw new RateLimitExceededException("Too many requests. Please try again in a bit.");
            }
            timestamps.addLast(now);
        }
    }

    @Scheduled(fixedRate = 10, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    void evictExpiredEntries() {
        // Every limit this service is called with is well under an hour, so
        // any deque with nothing added in the last hour is safe to drop
        // entirely — a fresh one is recreated on the next request for that
        // key if it's still active.
        long cutoff = System.currentTimeMillis() - Duration.ofHours(1).toMillis();
        requestLog.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            synchronized (timestamps) {
                return timestamps.isEmpty() || timestamps.peekLast() < cutoff;
            }
        });
    }
}
