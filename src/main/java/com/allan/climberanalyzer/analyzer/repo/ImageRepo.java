package com.allan.climberanalyzer.analyzer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.ExerciseImage;

public interface ImageRepo extends JpaRepository<ExerciseImage, Long> {
    Optional<ExerciseImage> findByFilename(String filename);
}
