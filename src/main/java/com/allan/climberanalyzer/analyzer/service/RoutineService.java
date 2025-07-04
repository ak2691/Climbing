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

import com.allan.climberanalyzer.analyzer.DTOClass.RoutineRequestDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.SelectedStyles;
import com.allan.climberanalyzer.analyzer.DTOClass.StyleChoiceDTO;
import com.allan.climberanalyzer.analyzer.model.AnswerChoice;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.model.HoldStyle;
import com.allan.climberanalyzer.analyzer.model.UserAssessment;
import com.allan.climberanalyzer.analyzer.model.UserResponse;
import com.allan.climberanalyzer.analyzer.repo.AnswerRepo;
import com.allan.climberanalyzer.analyzer.repo.AssessmentRepo;
import com.allan.climberanalyzer.analyzer.repo.ResponseRepo;
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

    public List<Map<String, String>> generateRoutine(RoutineRequestDTO responses) {
        UserAssessment userAssessment = new UserAssessment();
        userAssessment.setUserId(responses.getUserId());
        UserAssessment savedAssessment = assessmentRepo.save(userAssessment);
        List<UserResponse> responseList = getUserResponses(responses, savedAssessment);
        userAssessment.setResponses(responseList);

        List<Map<String, String>> exerciseList = responseList.stream().map(response -> {
            Map<String, String> exercise = new HashMap<>();
            exercise.put(response.getSelectedAnswer().getExercise().getExercise(),
                    response.getSelectedAnswer().getExercise().getDescription());
            return exercise;
        }).collect(Collectors.toList());

        return exerciseList;

    }

    public Map<String, Object> generateQuestions(SelectedStyles selectedStyles) throws IllegalAccessException {
        Map<String, Object> questions = new HashMap<>();
        Field[] fields = selectedStyles.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if ((Integer) field.get(selectedStyles) == 1) {
                String name = field.getName();

                List<Map<String, Long>> answers = getChoicesByStyleName(name);
                Map<String, Object> answerMap = new HashMap<>();
                answerMap.put("answer_choices", answers);
                questions.put(name, answerMap);

            }
        }

        return questions;

    }

    public List<Map<String, Long>> getChoicesByStyleName(String styleName) {

        return answerRepo.findAnswerChoicesByName(styleName).stream()
                .map(choice -> {
                    Map<String, Long> map = new HashMap<>();
                    map.put(choice.getAnswer(), choice.getId());

                    return map;

                })
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUserResponses(RoutineRequestDTO responses, UserAssessment userAssessment) {
        List<UserResponse> userResponses = responses.getResponses().stream().map(res -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setStyle(styleRepo.findByName(res.getStyle())
                    .orElseThrow(() -> new EntityNotFoundException("Style not found")));
            userResponse.setSelectedAnswer(answerRepo.findById(res.getAnswerId())
                    .orElseThrow(() -> new EntityNotFoundException("choice not found")));
            userResponse.setAssessment(userAssessment);
            UserResponse savedResponse = responseRepo.save(userResponse);

            return savedResponse;

        }).collect(Collectors.toList());
        return userResponses;
    }

}
