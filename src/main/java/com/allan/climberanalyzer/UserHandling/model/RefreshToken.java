package com.allan.climberanalyzer.UserHandling.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) // NEW: Store user ID
    private Long userId;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
