package com.allan.climberanalyzer.analyzer.repo;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allan.climberanalyzer.analyzer.model.RateLimitRequest;

import jakarta.transaction.Transactional;

public interface RateLimitRequestRepository extends JpaRepository<RateLimitRequest, Long> {
        @Query("SELECT COUNT(r) FROM RateLimitRequest r " +
                        "WHERE r.userId = :userId " +
                        "AND r.endpoint = :endpoint " +
                        "AND r.requestTime >= :cutoffTime")
        long countRecentRequests(@Param("userId") Long userId,
                        @Param("endpoint") String endpoint,
                        @Param("cutoffTime") LocalDateTime cutoffTime);

        @Modifying
        @Transactional
        @Query("DELETE FROM RateLimitRequest r WHERE r.requestTime < :cutoffTime")
        void deleteOldRequests(@Param("cutoffTime") LocalDateTime cutoffTime);
}
