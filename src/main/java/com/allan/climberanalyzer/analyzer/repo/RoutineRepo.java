package com.allan.climberanalyzer.analyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;

public interface RoutineRepo extends JpaRepository<RoutineModel, Long> {
    @Modifying
    @Query(value = "DELETE FROM routine_exercises WHERE routine_id = ?1", nativeQuery = true)
    void deleteRoutineExercises(Long routineId);

    @Modifying
    @Query(value = "INSERT INTO routine_exercises (routine_id, exercise_id) VALUES (?1, ?2)", nativeQuery = true)
    void insertRoutineExercise(Long routineId, Integer exerciseId);
}
