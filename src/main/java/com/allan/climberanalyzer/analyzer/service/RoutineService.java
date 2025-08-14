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
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineDisplayDTO;
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
import jakarta.transaction.Transactional;

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

    private int MAX_ROUTINES = 10;

    public String deleteRoutine(RoutineDisplayDTO deletedRoutine) {
        routineRepo.deleteById(deletedRoutine.getRoutine_id());
        return "Deleted routine id: " + deletedRoutine.getRoutine_id();
    }

    @Transactional
    public String editRoutine(RoutineDisplayDTO changedRoutine) {
        RoutineModel newroutine = routineRepo.findById(changedRoutine.getRoutine_id()).orElse(null);
        newroutine.setRoutine_name(changedRoutine.getRoutine_name());
        List<ExerciseModel> newexercises = changedRoutine.getExerciseList().stream().map(dto -> {
            ExerciseModel exercise = exercisesRepo.findById(dto.getExercise_id()).orElse(null);
            return exercise;
        }).collect(Collectors.toList());
        newroutine.setExercises(newexercises);
        routineRepo.save(newroutine);
        return "changed routine: " + newroutine.getRoutine_name();
    }

    public RoutineDisplayDTO saveRoutine(SavedRoutineDTO savedRoutine) {
        RoutineModel routine = new RoutineModel();
        UserProfile profile = userProfileRepo.findByUserId(savedRoutine.getUserId()).orElse(null);

        List<RoutineModel> routineList = routineRepo.findAllByUserProfile(profile).orElse(null);
        if (routineList.size() >= MAX_ROUTINES) {
            throw new RuntimeException("User has reached maximum routine limit of 10");
        }
        List<ExerciseModel> exercises = savedRoutine.getExerciseIds().stream()
                .map(id -> exercisesRepo.findById(id).orElse(null)).collect(Collectors.toList());
        routine.setExercises(exercises);
        UserProfile userprofile = userProfileRepo.findByUserId(savedRoutine.getUserId()).orElse(null);
        routine.setUserProfile(userprofile);
        if (savedRoutine.getRoutine_name() == null) {
            routine.setRoutine_name("new_routine");
        } else if (savedRoutine.getRoutine_name() != null && savedRoutine.getRoutine_name().length() == 0) {
            routine.setRoutine_name("new_routine");
        } else {
            routine.setRoutine_name(savedRoutine.getRoutine_name());
        }
        RoutineModel saved = routineRepo.save(routine);
        RoutineDisplayDTO routineDTO = routineToDTO(saved);
        return routineDTO;

    }

    public RoutineDisplayDTO routineToDTO(RoutineModel routine) {
        RoutineDisplayDTO dto = new RoutineDisplayDTO();
        List<ExerciseDisplayDTO> exerciseList = routine.getExercises().stream().map((exercise) -> {
            ExerciseDisplayDTO exerciseDTO = new ExerciseDisplayDTO();
            exerciseDTO.setName(exercise.getExercise());
            exerciseDTO.setDescription(exercise.getDescription());
            exerciseDTO.setExercise_id(exercise.getId());
            return exerciseDTO;
        }).collect(Collectors.toList());
        dto.setExerciseList(exerciseList);
        dto.setRoutine_id(routine.getRoutine_id());
        dto.setRoutine_name(routine.getRoutine_name());
        return dto;
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
