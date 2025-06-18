package com.allan.climberanalyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.allan.climberanalyzer.model.FingerStrengthModel;

@Component
public interface FingerRepo extends JpaRepository<FingerStrengthModel, Integer> {

    @Query(value = """
            SELECT boulder_grade_v FROM finger_strength
            ORDER BY ABS(bodyweight_percentage - :inputStrength)
            LIMIT 1
            """, nativeQuery = true)
    public int findClosestGrade(@Param("inputStrength") int inputStrength);
}
