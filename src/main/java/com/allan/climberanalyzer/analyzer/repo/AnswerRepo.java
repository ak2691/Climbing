package com.allan.climberanalyzer.analyzer.repo;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.allan.climberanalyzer.analyzer.model.AnswerChoice;

public interface AnswerRepo extends JpaRepository<AnswerChoice, Long> {
    @Query("SELECT s FROM AnswerChoice s WHERE s.style.name = :styleName")
    List<AnswerChoice> findAnswerChoicesByName(@Param("styleName") String styleName);

    Optional<AnswerChoice> findById(Long id);

}
