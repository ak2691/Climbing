package com.allan.climberanalyzer.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisModel {
    private String overHangAnalysis;
    private String verticalAnalysis;
    private String slabAnalysis;

}
