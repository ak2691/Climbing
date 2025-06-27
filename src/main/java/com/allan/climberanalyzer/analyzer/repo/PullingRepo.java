package com.allan.climberanalyzer.analyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.allan.climberanalyzer.analyzer.model.PullingStrengthModel;

@Component
public interface PullingRepo extends JpaRepository<PullingStrengthModel, Integer> {

    @Query(value = """
            SELECT boulder_grade_v FROM pulling_strength
            ORDER BY ABS(bodyweight_percentage - :inputStrength)
            LIMIT 1
            """, nativeQuery = true)
    public int findClosestGrade(@Param("inputStrength") int inputStrength);
}
