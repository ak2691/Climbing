package com.allan.climberanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.DTOClass.InputNumbers;

import com.allan.climberanalyzer.service.CalculateGradeService;

@RestController
public class GradeController {

    @Autowired
    CalculateGradeService calculateGradeService;

    @GetMapping("/calculator")
    public String getCalculator() {
        return "In the calculator";
    }

    @PostMapping("/calculator")
    public ResponseEntity<?> calculateClimbingGrade(@RequestBody InputNumbers numbers) {
        try {
            return new ResponseEntity<>(calculateGradeService.calculateClimbingGrade(numbers), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
