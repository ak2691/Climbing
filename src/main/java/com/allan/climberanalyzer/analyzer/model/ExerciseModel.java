package com.allan.climberanalyzer.analyzer.model;

import java.util.ArrayList;
import java.util.List;

import com.allan.climberanalyzer.UserHandling.model.RoutineModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "exercise_list")
public class ExerciseModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String exercise;
  private String description;

  @ManyToMany(mappedBy = "exercises")
  List<RoutineModel> routines;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  private List<AnswerChoice> answerChoice;

  @Column(columnDefinition = "text[]")
  private String[] keywords;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  private List<ExerciseImage> images = new ArrayList<>();
}
