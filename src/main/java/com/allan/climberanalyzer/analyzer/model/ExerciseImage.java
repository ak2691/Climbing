package com.allan.climberanalyzer.analyzer.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private ExerciseModel exercise;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ExerciseRequest exerciseRequest;

}
