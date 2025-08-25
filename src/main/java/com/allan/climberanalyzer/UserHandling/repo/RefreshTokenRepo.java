package com.allan.climberanalyzer.UserHandling.repo;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.UserHandling.model.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    void deleteByExpiryDateBefore(Date date);

    void deleteByUsername(String username);
}
