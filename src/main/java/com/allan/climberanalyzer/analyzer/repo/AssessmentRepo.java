package com.allan.climberanalyzer.analyzer.repo;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.UserAssessment;

public interface AssessmentRepo extends JpaRepository<UserAssessment, Long> {

}
