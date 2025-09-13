package com.allan.climberanalyzer.analyzer.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercise_request_images")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExerciseRequestImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String originalName;
    private String contentType;
    private Long fileSize;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "exercise_request_id")
    private ExerciseRequest exerciseRequest;

    public ExerciseImage toExerciseImage(ExerciseModel exercise) {
        ExerciseImage exerciseImage = new ExerciseImage();
        exerciseImage.setFilename(this.filename);
        exerciseImage.setOriginalFilename(this.originalName);
        exerciseImage.setContentType(this.contentType);
        exerciseImage.setFileSize(this.fileSize);
        exerciseImage.setExercise(exercise);
        return exerciseImage;
    }
}
