package com.allan.climberanalyzer.UserHandling.model;

import java.util.List;

import com.allan.climberanalyzer.analyzer.model.ExerciseModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saved_routines")
public class RoutineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routine_id;

    private String routine_name;

    @ManyToOne
    @JoinColumn(name = "user_profile")
    UserProfile userProfile;

    @ManyToMany
    @JoinTable(name = "routine_exercises", joinColumns = @JoinColumn(name = "routine_id"), inverseJoinColumns = @JoinColumn(name = "exercise_id"))
    List<ExerciseModel> exercises;

}
