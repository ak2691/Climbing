package com.allan.climberanalyzer.DTOClass;

import lombok.Data;

@Data
public class UserResult {
    private int overHangGrade;
    private int verticalGrade;
    private int slabGrade;
    private int calculatedFingerStrengthGrade;
    private int calculatedPullingStrengthGrade;
}
