package com.allan.climberanalyzer.analyzer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.ProfileDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.RoutineDisplayDTO;

@Service
public class ProfileService {

    @Autowired
    private UserProfileRepo userProfileRepo;

    public String editProfile(ProfileDTO profileDTO) {
        UserProfile userProfile = userProfileRepo.findByUserId(profileDTO.getUser_id()).orElse(null);
        userProfile.setVerticalGrade(profileDTO.getVerticalGrade());
        userProfile.setOverhangGrade(profileDTO.getOverhangGrade());
        userProfile.setSlabGrade(profileDTO.getSlabGrade());
        userProfile.setFingerStrengthGrade(profileDTO.getFingerStrengthGrade());
        userProfile.setPullingStrengthGrade(profileDTO.getPullingStrengthGrade());
        userProfile.setHeightIn((double) profileDTO.getHeightIn());
        userProfile.setWeightLb((double) profileDTO.getWeightLb());
        userProfileRepo.save(userProfile);
        return "Profile changed!";
    }

    public ProfileDTO getProfile(UserProfile userProfile) {
        ProfileDTO profile = new ProfileDTO();
        profile.setUsername(userProfile.getUser().getUsername());
        profile.setUser_id(userProfile.getUser().getId());
        profile.setFingerStrengthGrade(userProfile.getFingerStrengthGrade());
        profile.setPullingStrengthGrade(userProfile.getPullingStrengthGrade());
        profile.setVerticalGrade(userProfile.getVerticalGrade());
        profile.setOverhangGrade(userProfile.getOverhangGrade());
        profile.setSlabGrade(userProfile.getSlabGrade());
        if (userProfile.getHeightCm() != null) {
            profile.setHeightCm((int) Math.round(userProfile.getHeightCm()));
        }
        if (userProfile.getHeightIn() != null) {
            profile.setHeightIn((int) Math.round(userProfile.getHeightIn()));
        }
        if (userProfile.getWeightKg() != null) {
            profile.setWeightKg((int) Math.round(userProfile.getWeightKg()));
        }
        if (userProfile.getWeightLb() != null) {
            profile.setWeightLb((int) Math.round(userProfile.getWeightLb()));
        }
        if (userProfile.getRoutines() != null) {
            profile.setRoutines(getRoutineList(userProfile.getRoutines()));
        }
        //

        return profile;
    }

    public List<RoutineDisplayDTO> getRoutineList(List<RoutineModel> routines) {
        List<RoutineDisplayDTO> routineDisplay = routines.stream().map((routine) -> {
            RoutineDisplayDTO routineDTO = new RoutineDisplayDTO();
            routineDTO.setRoutine_id(routine.getRoutine_id());
            routineDTO.setRoutine_name(routine.getRoutine_name());
            List<ExerciseDisplayDTO> exerciseList = routine.getExercises().stream().map((exercise) -> {
                ExerciseDisplayDTO exerciseDTO = new ExerciseDisplayDTO();
                exerciseDTO.setName(exercise.getExercise());
                exerciseDTO.setDescription(exercise.getDescription());
                exerciseDTO.setExercise_id(exercise.getId());
                return exerciseDTO;
            }).collect(Collectors.toList());
            routineDTO.setExerciseList(exerciseList);
            return routineDTO;
        }).collect(Collectors.toList());
        return routineDisplay;

    }
}
