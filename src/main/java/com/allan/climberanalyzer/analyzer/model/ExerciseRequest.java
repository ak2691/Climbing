package com.allan.climberanalyzer.analyzer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "exercise_requests")
public class ExerciseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String reviewNotes;

    @OneToMany(mappedBy = "exerciseRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExerciseRequestImage> images = new ArrayList<>();

    public ExerciseModel toExercise() {
        ExerciseModel exercise = new ExerciseModel();
        exercise.setExercise(this.name);
        exercise.setDescription(this.description);
        return exercise;
    }

    public ExerciseRequest(String name, String description, Long userId) {
        this();
        this.name = name;
        this.description = description;
        this.userId = userId;
    }
}
