package com.allan.climberanalyzer.analyzer.controller;

import java.net.ResponseCache;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.analyzer.DTOClass.InputNumbers;
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineRequestDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SelectedStyles;
import com.allan.climberanalyzer.analyzer.DTOClass.StyleChoiceDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.service.AnalyzeResultService;
import com.allan.climberanalyzer.analyzer.service.AnalyzeResultsTwoService;
import com.allan.climberanalyzer.analyzer.service.CalculateGradeService;
import com.allan.climberanalyzer.analyzer.service.RoutineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AnalyzerController {

    @Autowired
    CalculateGradeService calculateGradeService;

    @Autowired
    AnalyzeResultService analyzeResultService;

    @Autowired
    AnalyzeResultsTwoService analyzeResultsTwoService;

    @Autowired
    RoutineService routineService;

    @GetMapping("/calculator")
    public String getCalculator() {
        return "In the calculator";
    }

    @PostMapping("/calculator")
    public ResponseEntity<?> calculateClimbingGrade(@RequestBody InputNumbers numbers) {
        try {

            List<Integer> ret = calculateGradeService.calculateClimbingGrade(numbers);

            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/analyzefirst")
    public ResponseEntity<?> analyzeResults(@RequestBody UserResult userResult) {
        try {
            return new ResponseEntity<>(analyzeResultService.analyzeBasicWeaknesses(userResult), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/analyzesecond")
    public ResponseEntity<?> analyzeResultsTwo(@RequestBody UserResult userResult) {
        try {
            return new ResponseEntity<>(analyzeResultsTwoService.analyzeSpecificWeaknesses(userResult), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/generatequestions")
    public ResponseEntity<?> generateQuestions(@RequestBody SelectedStyles selectedStyles) {
        try {
            Map<String, Object> questions = routineService.generateQuestions(selectedStyles);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generateroutine")
    public ResponseEntity<?> generateRoutine(@RequestBody RoutineRequestDTO responses) {
        try {
            List<Map<String, String>> exerciseList = routineService.generateRoutine(responses);
            return new ResponseEntity<>(exerciseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
