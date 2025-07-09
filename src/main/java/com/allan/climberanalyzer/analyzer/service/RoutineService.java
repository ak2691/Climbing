package com.allan.climberanalyzer.analyzer.service;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.QuestionnaireResults;
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineRequestDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SavedRoutineDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SelectedStyles;
import com.allan.climberanalyzer.analyzer.DTOClass.StyleChoiceDTO;
import com.allan.climberanalyzer.analyzer.model.AnswerChoice;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.model.HoldStyle;
import com.allan.climberanalyzer.analyzer.model.UserAssessment;
import com.allan.climberanalyzer.analyzer.model.UserResponse;
import com.allan.climberanalyzer.analyzer.repo.AnswerRepo;
import com.allan.climberanalyzer.analyzer.repo.AssessmentRepo;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;
import com.allan.climberanalyzer.analyzer.repo.ResponseRepo;
import com.allan.climberanalyzer.analyzer.repo.RoutineRepo;
import com.allan.climberanalyzer.analyzer.repo.StyleRepo;

import io.jsonwebtoken.lang.Arrays;
import jakarta.persistence.EntityNotFoundException;

@Service
public class RoutineService {

    @Autowired
    StyleRepo styleRepo;

    @Autowired
    AnswerRepo answerRepo;

    @Autowired
    ResponseRepo responseRepo;

    @Autowired
    AssessmentRepo assessmentRepo;

    @Autowired
    ExercisesRepo exercisesRepo;

    @Autowired
    RoutineRepo routineRepo;

    @Autowired
    UserProfileRepo userProfileRepo;

    public String saveRoutine(SavedRoutineDTO savedRoutine) {
        RoutineModel routine = new RoutineModel();
        List<ExerciseModel> exercises = savedRoutine.getExerciseIds().stream()
                .map(id -> exercisesRepo.findById(id).orElse(null)).collect(Collectors.toList());
        routine.setExercises(exercises);
        UserProfile userprofile = userProfileRepo.findByUserId(savedRoutine.getUserId()).orElse(null);
        routine.setUserProfile(userprofile);
        routineRepo.save(routine);
        return "Routine saved";

    }

    public QuestionnaireResults generateResults(RoutineRequestDTO responses) {
        UserAssessment userAssessment = new UserAssessment();
        userAssessment.setUserId(responses.getUserId());
        UserAssessment savedAssessment = assessmentRepo.save(userAssessment);
        List<UserResponse> responseList = getUserResponses(responses, savedAssessment);
        savedAssessment.setResponses(responseList);

        List<ExerciseModel> exerciseList = responseList.stream().map(response -> {

            return response.getSelectedAnswer().getExercise();
        }).collect(Collectors.toList());
        List<ExerciseDisplayDTO> displayList = exerciseList.stream().map(exercise -> {
            ExerciseDisplayDTO display = new ExerciseDisplayDTO();
            display.setName(exercise.getExercise());
            display.setDescription(exercise.getDescription());
            return display;
        }).collect(Collectors.toList());
        List<Integer> exerciseIds = exerciseList.stream().map(exercise -> exercise.getId())
                .collect(Collectors.toList());
        QuestionnaireResults questionnaireResults = new QuestionnaireResults();
        questionnaireResults.setExerciseList(exerciseIds);
        questionnaireResults.setDisplayExercises(displayList);
        questionnaireResults.setUserId(responses.getUserId());

        return questionnaireResults;

    }

    public Map<String, Object> generateQuestions(SelectedStyles selectedStyles) throws IllegalAccessException {
        Map<String, Object> questions = new HashMap<>();
        Field[] fields = selectedStyles.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if ((Integer) field.get(selectedStyles) == 1) {
                String name = field.getName();

                List<Map<String, String>> answers = getChoicesByStyleName(name);
                Map<String, Object> answerMap = new HashMap<>();
                answerMap.put("answer_choices", answers);
                questions.put(name, answerMap);

            }
        }

        return questions;

    }

    public List<Map<String, String>> getChoicesByStyleName(String styleName) {

        return answerRepo.findAnswerChoicesByName(styleName).stream()
                .map(choice -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("answer", choice.getAnswer());
                    map.put("answer_id", String.valueOf(choice.getId()));

                    return map;

                })
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUserResponses(RoutineRequestDTO responses, UserAssessment userAssessment) {
        List<UserResponse> userResponses = responses.getResponses().stream().map(res -> {
            UserResponse userResponse = new UserResponse();

            userResponse.setSelectedAnswer(answerRepo.findById(res)
                    .orElseThrow(() -> new EntityNotFoundException("choice not found")));
            userResponse.setAssessment(userAssessment);
            UserResponse savedResponse = responseRepo.save(userResponse);

            return savedResponse;

        }).collect(Collectors.toList());
        return userResponses;
    }

}
