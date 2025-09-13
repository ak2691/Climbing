package com.allan.climberanalyzer.UserHandling.repo;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.allan.climberanalyzer.UserHandling.model.User;

import jakarta.transaction.Transactional;

@Component
public interface UserRepo extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    @Query("SELECT u.id from User u WHERE u.username = :username")
    public Optional<Long> findIdByUsername(@Param("username") String username);

    public Boolean existsByUsername(String username);

    public Boolean existsByEmail(String email);

    public Optional<User> findByVerificationCode(String verificationCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.enabled = false AND u.verificationExpiration < :expirationTime")
    int deleteByEnabledFalseAndVerificationExpirationBefore(@Param("expirationTime") LocalDateTime expirationTime);
}
