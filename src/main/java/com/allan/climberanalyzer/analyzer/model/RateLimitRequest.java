package com.allan.climberanalyzer.analyzer.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rate_limit_requests")
@Data
public class RateLimitRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "endpoint", nullable = false)
    private String endpoint; // e.g., "chat", "image-upload"

    @CreationTimestamp
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    // Constructors
    public RateLimitRequest() {
    }

    public RateLimitRequest(Long userId, String endpoint) {
        this.userId = userId;
        this.endpoint = endpoint;
    }
}
