package com.allan.climberanalyzer.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisModelTwo {
    private String fingerAnalysis;

    private String pullingAnalysis;
    private String fingerSeverity;
    private String pullingSeverity;
}
