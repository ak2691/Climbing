package com.allan.climberanalyzer.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private UserAssessment assessment;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private HoldStyle style;

    @ManyToOne
    @JoinColumn(name = "answer_choice_id")
    private AnswerChoice selectedAnswer;
}
