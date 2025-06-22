package com.allan.climberanalyzer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.allan.climberanalyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.repo.ExercisesRepo;
import com.allan.climberanalyzer.repo.WeaknessesRepo;

public class AnalyzeResultService {

    @Autowired
    WeaknessesRepo weaknessRepo;

    @Autowired
    ExercisesRepo exerciseRepo;

    public List<String> analyzeBasicWeaknesses(UserResult userResult) {
        int overHangGrade = userResult.getOverHangGrade();
        int verticalGrade = userResult.getVerticalGrade();
        int slabGrade = userResult.getSlabGrade();
        int calculatedFingerStrengthGrade = userResult.getCalculatedFingerStrengthGrade();
        int calculatedPullingStrengthGrade = userResult.getCalculatedPullingStrengthGrade();
        // logic to deal with all these numbers
        return new ArrayList<>(Arrays.asList("test"));
        //
    }
}
