package com.allan.climberanalyzer.analyzer.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.analyzer.model.RateLimitRequest;
import com.allan.climberanalyzer.analyzer.repo.RateLimitRequestRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DatabaseRateLimiter {
    @Autowired
    private RateLimitRequestRepository rateLimitRepository;

    /**
     * Check if user is allowed to make a request
     * 
     * @param userId            - user identifier
     * @param endpoint          - endpoint being accessed (e.g., "chat", "upload")
     * @param maxRequests       - maximum requests allowed
     * @param timeWindowMinutes - time window in minutes
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowed(Long userId, String endpoint, int maxRequests, int timeWindowMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(timeWindowMinutes);

        // Count requests in the time window
        long recentRequests = rateLimitRepository.countRecentRequests(userId, endpoint, cutoffTime);

        if (recentRequests >= maxRequests) {
            return false; // Rate limited
        }

        // Record this request
        rateLimitRepository.save(new RateLimitRequest(userId, endpoint));
        return true;
    }

    /**
     * Convenience method for chat endpoint specifically
     */
    public boolean isChatAllowed(Long userId) {
        return isAllowed(userId, "chat", 10, 60); // 10 requests per hour
    }

    /**
     * Get remaining requests for user
     */
    public int getRemainingRequests(Long userId, String endpoint, int maxRequests, int timeWindowMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(timeWindowMinutes);
        long recentRequests = rateLimitRepository.countRecentRequests(userId, endpoint, cutoffTime);
        return Math.max(0, maxRequests - (int) recentRequests);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanUpOrphanedRequests() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(60);
        rateLimitRepository.deleteOldRequests(cutoffTime);
    }

}
