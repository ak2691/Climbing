package com.allan.climberanalyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PullingStrengthModel {
    @Id
    private int id;
    private int bouldering_grade_v;
    private int bodyweight_percentage;

}
