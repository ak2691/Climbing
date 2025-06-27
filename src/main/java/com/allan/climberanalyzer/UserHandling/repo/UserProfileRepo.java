package com.allan.climberanalyzer.UserHandling.repo;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.allan.climberanalyzer.UserHandling.model.UserProfile;

@Component
public interface UserProfileRepo extends JpaRepository<UserProfile, BigInteger> {
    Optional<UserProfile> findByUserId(Long userId);
}
