package com.allan.climberanalyzer.analyzer.DTOClass;

import com.allan.climberanalyzer.analyzer.model.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewExerciseRequest {
    private RequestStatus status;
    private String reviewNotes;
    private String name;
    private String description;
    private Long requestId;
}
