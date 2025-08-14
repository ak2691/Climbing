package com.allan.climberanalyzer.analyzer.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.ExerciseRequestImage;

public interface ExerciseRequestImageRepo extends JpaRepository<ExerciseRequestImage, Long> {
    Optional<ExerciseRequestImage> findByFilename(String filename);

    List<ExerciseRequestImage> findByExerciseRequestIsNull();
}
