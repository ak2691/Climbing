package com.allan.climberanalyzer.analyzer.DTOClass;

import java.util.List;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String username;

    private Long user_id;

    private int fingerStrengthGrade;

    private int pullingStrengthGrade;

    private int verticalGrade;

    private int overhangGrade;

    private int slabGrade;

    private int heightCm;

    private int heightIn;

    private int weightKg;

    private int weightLb;

    private List<RoutineDisplayDTO> routines;
}
