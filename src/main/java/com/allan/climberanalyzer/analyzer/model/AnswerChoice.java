package com.allan.climberanalyzer.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "answer_choices")
public class AnswerChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private HoldStyle style;

    private String answer;

    @OneToOne
    @JoinColumn(name = "exercise_id")
    ExerciseModel exercise;

    @Enumerated(EnumType.STRING)
    private WeaknessType weaknessType;
}
