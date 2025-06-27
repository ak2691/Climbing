package com.allan.climberanalyzer.analyzer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.analyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;

@Service
public class AnalyzeResultsTwoService {

    @Autowired
    ExercisesRepo exerciseRepo;

    @Autowired
    RecommendationsService recommendationsService;

    public int climberLevel(int vertical, int overhang) {
        int grade = Math.max(vertical, overhang);
        int level = 1;
        if (grade >= 0 && grade < 4) {
            level = 1;
        } else if (grade >= 4 && grade < 7) {
            level = 2;
        } else if (grade >= 7) {
            level = 3;
        }
        return level;
    }

    public List<Integer> deltaPhaseTwo(int fingerGrade, int pullingGrade, int level, int vertical, int overhang) {
        List<Integer> deltas = new ArrayList<>(Arrays.asList(0, 0));
        int maxGrade = Math.max(vertical, overhang);
        if (level == 1) {
            deltas.set(0, (int) Math.round((fingerGrade - maxGrade) * 0.5));
            deltas.set(1, (int) Math.round((pullingGrade - maxGrade) * 0.5));
        } else if (level == 2) {
            deltas.set(0, (int) Math.round((fingerGrade - maxGrade) * 0.75));
            deltas.set(1, (int) Math.round((pullingGrade - maxGrade) * 0.75));
        } else if (level == 3) {
            deltas.set(0, (int) Math.round((fingerGrade - maxGrade) * 1));
            deltas.set(1, (int) Math.round((pullingGrade - maxGrade) * 1));
        }
        return deltas;

    }

    public List<Map<String, Object>> analyzeSpecificWeaknesses(UserResult userResult) {
        int fingerGrade = userResult.getCalculatedFingerStrengthGrade();
        int pullingGrade = userResult.getCalculatedPullingStrengthGrade();
        int vertical = userResult.getVerticalGrade();
        int overhang = userResult.getOverHangGrade();
        int level = climberLevel(vertical, overhang);
        List<Map<String, Object>> analysis = new ArrayList<>();

        List<Integer> deltas = deltaPhaseTwo(fingerGrade, pullingGrade, level, vertical, overhang);
        if (deltas.get(0) == -1) {
            analysis.add(recommendationsService.getRecommendation("finger_strength_weakness", "minor"));
        } else if (deltas.get(0) == -2) {
            analysis.add(recommendationsService.getRecommendation("finger_strength_weakness", "moderate"));
        } else if (deltas.get(0) == -3) {
            analysis.add(recommendationsService.getRecommendation("finger_strength_weakness", "major"));
        } else if (deltas.get(0) == 1) {
            analysis.add(recommendationsService.getRecommendation("skill_deficit", "minor"));
        } else if (deltas.get(0) == 2) {
            analysis.add(recommendationsService.getRecommendation("skill_deficit", "moderate"));
        } else if (deltas.get(0) == 3) {
            analysis.add(recommendationsService.getRecommendation("skill_deficit", "major"));
        }
        if (deltas.get(1) == -1) {
            analysis.add(recommendationsService.getRecommendation("pull_strength_weakness", "minor"));
        } else if (deltas.get(1) == -2) {
            analysis.add(recommendationsService.getRecommendation("pull_strength_weakness", "moderate"));
        } else if (deltas.get(1) == -3) {
            analysis.add(recommendationsService.getRecommendation("pull_strength_weakness", "major"));
        }
        return analysis;

    }

}
