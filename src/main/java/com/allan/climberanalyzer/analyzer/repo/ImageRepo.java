package com.allan.climberanalyzer.analyzer.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allan.climberanalyzer.analyzer.model.ExerciseImage;

import jakarta.transaction.Transactional;

public interface ImageRepo extends JpaRepository<ExerciseImage, Long> {
    Optional<ExerciseImage> findByFilename(String filename);

    void deleteByFilename(String filename);

    @Query("DELETE FROM ExerciseImage ei WHERE ei.createdAt < :cutoffTime " +
            "AND ei.exercise IS NULL AND ei.exerciseRequest IS NULL")
    @Modifying
    @Transactional
    int deleteOrphanedImages(@Param("cutoffTime") LocalDateTime cutoffTime);
}
