package com.allan.climberanalyzer.analyzer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.boot.model.relational.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.model.ExerciseImage;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.model.ExerciseRequestImage;
import com.allan.climberanalyzer.analyzer.repo.ExerciseRequestImageRepo;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;
import com.allan.climberanalyzer.analyzer.repo.ImageRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ImageService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private ExercisesRepo exercisesRepo;

    @Autowired
    DatabaseRateLimiter rateLimiter;

    @Autowired
    JwtService jwtService;

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    public String saveTemporaryImage(MultipartFile file) {

        String filename = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());

        storageService.saveFile(file, filename);

        ExerciseImage exerciseImage = new ExerciseImage();
        exerciseImage.setFilename(filename);
        exerciseImage.setOriginalFilename(file.getOriginalFilename());
        exerciseImage.setContentType(file.getContentType());
        exerciseImage.setFileSize(file.getSize());
        imageRepo.save(exerciseImage);
        return filename;

    }

    public void associateImagesWithExercise(String description, Integer exerciseId) {
        ExerciseModel exercise = exercisesRepo.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        Pattern pattern = Pattern.compile("src=\"/api/images/([^\"]+)\"");
        Matcher matcher = pattern.matcher(description);

        while (matcher.find()) {
            String filename = matcher.group(1);
            ExerciseImage image = imageRepo.findByFilename(filename)
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            if (image.getExercise() == null) {
                image.setExercise(exercise);
                imageRepo.save(image);
            }
        }

    }

    public void deleteTemporaryImageRecord(Long imageId) {
        if (imageRepo.existsById(imageId)) {
            imageRepo.deleteById(imageId);
        } else {
            throw new EntityNotFoundException("Image with ID " + imageId + " not found");
        }

    }

    public String getContentType(String filename) {
        ExerciseImage image = imageRepo.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return image.getContentType();
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    @Scheduled(fixedRate = 1800000) // Every 30 minutes
    public void cleanupOrphanedExerciseImages() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusSeconds(10);

        try {
            int deletedCount = imageRepo.deleteOrphanedImages(oneHourAgo);

            if (deletedCount > 0) {
                log.info("Cleaned up {} orphaned exercise images older than 1 hour", deletedCount);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup orphaned exercise images", e);
        }
    }
}
