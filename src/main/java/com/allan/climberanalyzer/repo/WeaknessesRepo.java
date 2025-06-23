package com.allan.climberanalyzer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allan.climberanalyzer.model.WeaknessModel;

public interface WeaknessesRepo extends JpaRepository<WeaknessModel, Integer> {

}
