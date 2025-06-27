package com.allan.climberanalyzer.analyzer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.analyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.analyzer.model.AnalysisModel;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;

@Service

public class AnalyzeResultService {

    @Autowired
    AnalysisModel analysisModel;

    public int overHangPhaseOne(int fingerGrade, int pullingGrade, int grade) {
        int expectedGrade = (int) Math.round(fingerGrade * 0.6 + pullingGrade * 0.4);
        int delta = 0;
        if (grade > expectedGrade) {
            delta = -1;
        } else if (grade < expectedGrade) {
            delta = 1;
        }
        return delta;
    }

    public int verticalPhaseOne(int fingerGrade, int pullingGrade, int grade) {
        int expectedGrade = (int) Math.round(fingerGrade * 0.75 + pullingGrade * 0.25);
        int delta = 0;
        if (grade > expectedGrade) {
            delta = -1;
        } else if (grade < expectedGrade) {
            delta = 1;
        }
        return delta;
    }

    // In progress

    public AnalysisModel analyzeBasicWeaknesses(UserResult userResult) {
        int overHangGrade = userResult.getOverHangGrade();
        int verticalGrade = userResult.getVerticalGrade();
        int slabGrade = userResult.getSlabGrade();
        int calculatedFingerStrengthGrade = userResult.getCalculatedFingerStrengthGrade();
        int calculatedPullingStrengthGrade = userResult.getCalculatedPullingStrengthGrade();
        int overallGrade = (int) Math.round(((double) overHangGrade + verticalGrade) / 2);
        // logic to deal with all these numbers

        // Prioritize finger strength and pulling strength
        int phaseOneOverHang = overHangPhaseOne(calculatedFingerStrengthGrade, calculatedPullingStrengthGrade,
                overHangGrade);
        int phaseOneVertical = verticalPhaseOne(calculatedFingerStrengthGrade, calculatedPullingStrengthGrade,
                verticalGrade);

        // hard coding stuff testing for now

        if (phaseOneOverHang == -1) {
            analysisModel.setOverHangAnalysis(
                    "Out-climbing your physical strengths. Good technique, could use additional strength training.");

        } else if (phaseOneOverHang == 1) {
            analysisModel
                    .setOverHangAnalysis("Strong, but can use additional technique work. Tension, core, beta reading.");
        } else {
            analysisModel.setOverHangAnalysis("On-par with your expected grade! Keep on climbing.");
        }
        if (phaseOneVertical == -1) {
            analysisModel.setVerticalAnalysis(
                    "Out-climbing your physical strengths. Good technique, could use additional strength training.");
        } else if (phaseOneOverHang == 1) {
            analysisModel.setVerticalAnalysis(
                    "Strong, but can use additional technique work. Beta reading, better use of legs, flagging, body positioning.");
        } else {
            analysisModel.setVerticalAnalysis("On-par with your expected grade! Keep on climbing.");
        }
        if (slabGrade < overallGrade) {
            analysisModel.setSlabAnalysis(
                    "Your slab climbing is relatively weak compared to your other styles. Balance, footwork, and body positioning are important skills to work on");

        } else if (slabGrade > overallGrade) {
            analysisModel.setSlabAnalysis(
                    "Your slab climbing is relatively weak compared to your other styles. Balance, footwork, and body positioning are important skills to work on");

        } else {
            analysisModel.setSlabAnalysis("On-par with your expected grade! Keep on climbing.");
        }

        //

        return analysisModel;
        //
    }
}
