package com.allan.climberanalyzer.analyzer.repo;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.UserResponse;

public interface ResponseRepo extends JpaRepository<UserResponse, Long> {

}
