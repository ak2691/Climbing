package com.allan.climberanalyzer.analyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.ExerciseModel;

public interface ExercisesRepo extends JpaRepository<ExerciseModel, Integer> {

}
