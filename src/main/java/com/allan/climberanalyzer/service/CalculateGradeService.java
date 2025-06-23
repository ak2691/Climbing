package com.allan.climberanalyzer.service;

import java.util.ArrayList;
import java.util.List;

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

    // Edge factor determined from Eva lopez protocols, Vigouroux studies on four
    // finger hangs, and Beastmaker test data
    public double calculateEdgeFactor(int edgeSize) {
        return Math.exp(-0.06 * (20 - edgeSize));
    }

    // Rohmert's curve data estimation
    public double calculateTimeFactor(int hangTime) {
        return Math.pow((double) 7 / hangTime, 0.2);
    }

    public int calculateFingerBodyWeightPercentage(int fingerStrength, int bodyweight, int edgeSize, int hangTime) {
        double edgeFactor = calculateEdgeFactor(edgeSize);
        double timeFactor = calculateTimeFactor(hangTime);
        double mvc7 = (double) fingerStrength / bodyweight;
        return (int) (Math.round((mvc7 / (edgeFactor * timeFactor)) * 100));

    }

    // Conversion of one rep max of Epley's formula to two rep max for the purpose
    // of our data. 1 / (1+ (2/30))
    public int EpleyFormulaTwoRepMax(double num, int reps) {
        return (int) Math.round((num * (1 + (reps / 30.0)) * (15.0 / 16.0)) * 100);
    }

    public List<Integer> calculateClimbingGrade(InputNumbers numbers) {
        bodyweight = numbers.getBodyweight();
        fingerStrength = numbers.getFingerStrength();
        pullingStrength = numbers.getPullingStrength();
        int hangTime = numbers.getHangTime();
        int edgeSize = numbers.getEdgeSize();
        int reps = numbers.getReps();
        fingerStrengthByWeight = calculateFingerBodyWeightPercentage(fingerStrength, bodyweight, edgeSize,
                hangTime) + 100;
        pullingStrengthByWeight = (int) EpleyFormulaTwoRepMax((double) pullingStrength / bodyweight, reps) + 100;
        int fingerGrade = fingerRepo.findClosestGrade(fingerStrengthByWeight);
        int pullingGrade = pullingRepo.findClosestGrade(pullingStrengthByWeight);
        int overallGrade = (fingerGrade + pullingGrade) / 2;

        List<Integer> ret = new ArrayList<>();
        if (fingerGrade >= 17) {
            fingerGrade = 17;
        }
        if (pullingGrade >= 17) {
            pullingGrade = 17;
        }
        if (overallGrade >= 17) {
            overallGrade = 17;
        }
        System.out.println(pullingGrade);
        ret.add(numbers.getOverHangGrade());
        ret.add(numbers.getVerticalGrade());//
        ret.add(numbers.getSlabGrade());
        ret.add(fingerGrade);
        ret.add(pullingGrade);
        ret.add(overallGrade);
        return ret;
    }

}
