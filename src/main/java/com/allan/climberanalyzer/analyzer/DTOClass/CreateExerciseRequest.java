package com.allan.climberanalyzer.analyzer.DTOClass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExerciseRequest {
    @NotBlank(message = "Exercise name is required")
    @Size(max = 100, message = "Exercise name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 8000, message = "Description must be between 10 and 8000 characters")
    private String description;
}
