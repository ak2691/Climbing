package com.allan.climberanalyzer.analyzer.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allan.climberanalyzer.analyzer.model.ExerciseModel;

public interface ExercisesRepo extends JpaRepository<ExerciseModel, Integer> {

    @Query(value = "SELECT * FROM exercise_list WHERE keywords && CAST(:keywords AS text[])", nativeQuery = true)
    public List<ExerciseModel> findExercisesByKeywords(@Param("keywords") String keywords);
}
