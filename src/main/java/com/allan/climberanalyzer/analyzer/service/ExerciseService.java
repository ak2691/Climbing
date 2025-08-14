package com.allan.climberanalyzer.analyzer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.analyzer.DTOClass.CreateExerciseRequest;
import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;

@Service
public class ExerciseService {

    @Autowired
    ExercisesRepo exercisesRepo;

    @Autowired
    ImageService imageService;

    public List<ExerciseDisplayDTO> getExercises() {
        List<ExerciseModel> exerciseModels = exercisesRepo.findAll();
        List<ExerciseDisplayDTO> exerciseDisplays = exerciseModels.stream().map((exercise) -> {
            ExerciseDisplayDTO display = new ExerciseDisplayDTO();
            display.setName(exercise.getExercise());
            display.setExercise_id(exercise.getId());
            display.setDescription(exercise.getDescription());
            return display;
        }).collect(Collectors.toList());
        return exerciseDisplays;

    }

    public List<ExerciseDisplayDTO> convertExerciseDTO(List<ExerciseModel> exerciseModels) {
        List<ExerciseDisplayDTO> exerciseDisplays = exerciseModels.stream().map((exercise) -> {
            ExerciseDisplayDTO display = new ExerciseDisplayDTO();
            display.setName(exercise.getExercise());
            display.setExercise_id(exercise.getId());
            display.setDescription(exercise.getDescription());
            return display;
        }).collect(Collectors.toList());
        return exerciseDisplays;
    }

    private ExerciseDisplayDTO convertToDTO(ExerciseModel exercise) {
        ExerciseDisplayDTO display = new ExerciseDisplayDTO();
        display.setName(exercise.getExercise());
        display.setExercise_id(exercise.getId());
        display.setDescription(exercise.getDescription());
        return display;
    }

    public ExerciseDisplayDTO createExercise(CreateExerciseRequest request) {
        ExerciseModel exercise = new ExerciseModel();
        exercise.setExercise(request.getName());
        exercise.setDescription(request.getDescription());

        ExerciseModel savedExercise = exercisesRepo.save(exercise);

        imageService.associateImagesWithExercise(request.getDescription(),
                savedExercise.getId());

        ExerciseDisplayDTO display = new ExerciseDisplayDTO();
        display.setDescription(savedExercise.getDescription());
        display.setExercise_id(savedExercise.getId());
        display.setName(savedExercise.getExercise());

        return display;

    }

}
