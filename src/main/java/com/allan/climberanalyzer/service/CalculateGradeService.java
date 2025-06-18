package com.allan.climberanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.DTOClass.InputNumbers;

import com.allan.climberanalyzer.repo.FingerRepo;
import com.allan.climberanalyzer.repo.PullingRepo;

@Service
public class CalculateGradeService {
    @Autowired
    FingerRepo fingerRepo;

    @Autowired
    PullingRepo pullingRepo;

    private int fingerStrength;
    private int pullingStrength;
    private int bodyweight;
    private int fingerStrengthByWeight;
    private int pullingStrengthByWeight;

    public String calculateClimbingGrade(InputNumbers numbers) {
        bodyweight = numbers.getBodyweight();
        fingerStrength = numbers.getFingerStrength();
        pullingStrength = numbers.getPullingStrength();
        fingerStrengthByWeight = (int) Math.round(((double) fingerStrength / bodyweight) * 100) + 100;
        pullingStrengthByWeight = (int) Math.round(((double) pullingStrength / bodyweight) * 100) + 100;
        int fingerGrade = fingerRepo.findClosestGrade(fingerStrengthByWeight);
        int pullingGrade = pullingRepo.findClosestGrade(pullingStrengthByWeight);

        return "Grade for your finger strength: " + fingerGrade + " \nGrade for your pulling strength: " +
                pullingGrade + " \nOverall grade for your strength levels: " + (fingerGrade + pullingGrade) / 2;
    }

}
