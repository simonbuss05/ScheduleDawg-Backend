package com.simon.scheduledawg.service;

import com.simon.scheduledawg.exception.RateLimitExceededException;
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
}
