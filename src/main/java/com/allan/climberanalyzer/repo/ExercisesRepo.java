package com.allan.climberanalyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.model.ExerciseModel;

public interface ExercisesRepo extends JpaRepository<ExerciseModel, Integer> {

}
