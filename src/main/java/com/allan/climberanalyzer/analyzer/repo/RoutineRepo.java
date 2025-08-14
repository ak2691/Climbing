package com.allan.climberanalyzer.analyzer.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;

public interface RoutineRepo extends JpaRepository<RoutineModel, Long> {
    Optional<List<RoutineModel>> findAllByUserProfile(UserProfile userProfile);
}
