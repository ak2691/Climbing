package com.allan.climberanalyzer.analyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;

public interface RoutineRepo extends JpaRepository<RoutineModel, Long> {

}
