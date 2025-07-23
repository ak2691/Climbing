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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.InputNumbers;
import com.allan.climberanalyzer.analyzer.DTOClass.ProfileDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.QuestionnaireResults;
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineRequestDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SavedRoutineDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SelectedStyles;
import com.allan.climberanalyzer.analyzer.DTOClass.StyleChoiceDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.UserResult;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.service.AnalyzeResultService;
import com.allan.climberanalyzer.analyzer.service.AnalyzeResultsTwoService;
import com.allan.climberanalyzer.analyzer.service.CalculateGradeService;
import com.allan.climberanalyzer.analyzer.service.ExerciseService;
import com.allan.climberanalyzer.analyzer.service.ProfileService;
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

    @Autowired
    JwtService jwtService;

    @Autowired
    ProfileService profileService;

    @Autowired
    ExerciseService exerciseService;

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

    @PostMapping("/userid")
    public ResponseEntity<?> getUserIdFromJwt(@RequestBody String jwt) {
        try {
            Long userid = jwtService.getUserIdFromToken(jwt);
            return new ResponseEntity<>(userid, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generateroutine")
    public ResponseEntity<?> generateRoutine(@RequestBody RoutineRequestDTO responses) {
        try {
            QuestionnaireResults results = routineService.generateResults(responses);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/saveroutine")
    public ResponseEntity<?> saveRoutine(@RequestBody SavedRoutineDTO savedRoutine) {
        try {
            String message = routineService.saveRoutine(savedRoutine);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<?> displayProfile(@RequestBody String jwt) {
        try {
            UserProfile userProfile = jwtService.getUserProfileFromToken(jwt);
            ProfileDTO profileDTO = profileService.getProfile(userProfile);
            return new ResponseEntity<>(profileDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/exercises")
    public ResponseEntity<?> getExercises() {
        try {
            List<ExerciseDisplayDTO> exerciseDisplayDTOs = exerciseService.getExercises();
            return new ResponseEntity<>(exerciseDisplayDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/editroutine")
    public ResponseEntity<?> editRoutine(@RequestBody RoutineDisplayDTO changedRoutine) {
        try {
            String message = routineService.editRoutine(changedRoutine);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteroutine")
    public ResponseEntity<?> deleteRoutine(@RequestBody RoutineDisplayDTO deletedRoutine) {
        try {
            String message = routineService.deleteRoutine(deletedRoutine);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
