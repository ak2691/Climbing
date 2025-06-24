package com.allan.climberanalyzer.controller;

import java.net.ResponseCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.DTOClass.InputNumbers;
import com.allan.climberanalyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.service.AnalyzeResultService;
import com.allan.climberanalyzer.service.AnalyzeResultsTwoService;
import com.allan.climberanalyzer.service.CalculateGradeService;

@CrossOrigin
@RestController
public class AnalyzerController {

    @Autowired
    CalculateGradeService calculateGradeService;

    @Autowired
    AnalyzeResultService analyzeResultService;

    @Autowired
    AnalyzeResultsTwoService analyzeResultsTwoService;

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

    @PostMapping("/api/analyzefirst")
    public ResponseEntity<?> analyzeResults(@RequestBody UserResult userResult) {
        try {
            return new ResponseEntity<>(analyzeResultService.analyzeBasicWeaknesses(userResult), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/analyzesecond")
    public ResponseEntity<?> analyzeResultsTwo(@RequestBody UserResult userResult) {
        try {
            return new ResponseEntity<>(analyzeResultsTwoService.analyzeSpecificWeaknesses(userResult), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
