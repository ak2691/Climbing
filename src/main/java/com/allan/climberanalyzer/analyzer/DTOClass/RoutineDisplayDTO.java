package com.allan.climberanalyzer.analyzer.DTOClass;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineDisplayDTO {
    List<ExerciseDisplayDTO> exerciseList;
    Long routine_id;
    String routine_name;
}
