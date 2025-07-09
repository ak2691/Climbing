package com.allan.climberanalyzer.analyzer.DTOClass;

import java.util.List;

import com.allan.climberanalyzer.analyzer.model.ExerciseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireResults {
    private Long userId;
    private List<ExerciseDisplayDTO> displayExercises;
    private List<Integer> exerciseList;

}
