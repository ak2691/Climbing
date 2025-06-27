package com.allan.climberanalyzer.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FingerStrengthModel {
    @Id
    private int id;
    private int bouldering_grade_v;
    private int bodyweight_percentage;
}
