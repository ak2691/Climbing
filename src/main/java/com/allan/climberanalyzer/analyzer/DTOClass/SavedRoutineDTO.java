package com.allan.climberanalyzer.analyzer.DTOClass;

import java.util.List;

import com.allan.climberanalyzer.analyzer.model.ExerciseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedRoutineDTO {
    List<Integer> exerciseIds;
    Long userId;
    String routine_name;
}
