package com.allan.climberanalyzer.analyzer.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.ExerciseRequest;
import com.allan.climberanalyzer.analyzer.model.RequestStatus;

public interface ExerciseRequestRepo extends JpaRepository<ExerciseRequest, Long> {

    Optional<ExerciseRequest> findByUserIdAndStatus(Long userId, RequestStatus status);

    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);

    List<ExerciseRequest> findByStatusOrderByCreatedAtAsc(RequestStatus status);

    List<ExerciseRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
}
