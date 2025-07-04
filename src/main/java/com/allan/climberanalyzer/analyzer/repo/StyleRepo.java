package com.allan.climberanalyzer.analyzer.repo;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.analyzer.model.HoldStyle;

public interface StyleRepo extends JpaRepository<HoldStyle, Long> {
    Optional<HoldStyle> findByName(String name);
}
