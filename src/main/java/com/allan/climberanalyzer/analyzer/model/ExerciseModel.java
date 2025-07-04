package com.allan.climberanalyzer.analyzer.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "exercise_list")
public class ExerciseModel {
    @Id
    private int id;
    private String exercise;
    private String description;

    @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL)
    private AnswerChoice answerChoice;
}
