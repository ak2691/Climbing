package com.allan.climberanalyzer.analyzer.DTOClass;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineRequestDTO {
    private List<Long> responses;
    private Long userId;
}
