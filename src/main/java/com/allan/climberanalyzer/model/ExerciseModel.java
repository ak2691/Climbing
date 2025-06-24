package com.allan.climberanalyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExerciseModel {
    @Id
    private int id;
    private String exercise;
    private String category;
    private String priority;
}
