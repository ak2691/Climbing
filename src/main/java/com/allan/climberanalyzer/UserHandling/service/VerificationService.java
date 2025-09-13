package com.allan.climberanalyzer.UserHandling.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.UserHandling.repo.UserRepo;

import jakarta.websocket.server.ServerEndpoint;

@Service
public class VerificationService {
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    @Autowired
    UserRepo userRepo;

    public String generateVerificationCode() {
        // Generate 6-digit code
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredVerificationCodes() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = userRepo.deleteByEnabledFalseAndVerificationExpirationBefore(now);

            if (deletedCount > 0) {
                logger.info("Cleaned up {} expired unverified accounts", deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error during verification cleanup: ", e);
        }
    }
}
