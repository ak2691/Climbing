package com.allan.climberanalyzer.analyzer.DTOClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GptRequest {
    private String jwtToken;
    private String message;
    private String model = "gpt-4o-mini";
    private int maxTokens = 50;
    private double temperature = 0.7;
}
