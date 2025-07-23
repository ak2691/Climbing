package com.allan.climberanalyzer.analyzer.DTOClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GptResponse {
    public String response;
    public String model;
    private int tokensUsed;

}
