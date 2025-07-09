package com.allan.climberanalyzer.UserHandling.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "finger_strength_grade")
    private String fingerStrengthGrade;

    @Column(name = "pulling_strength_grade")
    private String pullingStrengthGrade;

    @Column(name = "vertical_grade")
    private String verticalGrade;

    @Column(name = "overhang_grade")
    private String overhangGrade;

    @Column(name = "slab_grade")
    private String slabGrade;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "height_in")
    private Double heightIn;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "weight_lb")
    private Double weightLb;

    @OneToMany(mappedBy = "userProfile")
    private List<RoutineModel> routines;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserProfile() {
    }

    public UserProfile(User user) {
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFingerStrengthGrade() {
        return fingerStrengthGrade;
    }

    public void setFingerStrengthGrade(String fingerStrengthGrade) {
        this.fingerStrengthGrade = fingerStrengthGrade;
    }

    public String getPullingStrengthGrade() {
        return pullingStrengthGrade;
    }

    public void setPullingStrengthGrade(String pullingStrengthGrade) {
        this.pullingStrengthGrade = pullingStrengthGrade;
    }

    public String getVerticalGrade() {
        return verticalGrade;
    }

    public void setVerticalGrade(String verticalGrade) {
        this.verticalGrade = verticalGrade;
    }

    public String getOverhangGrade() {
        return overhangGrade;
    }

    public void setOverhangGrade(String overhangGrade) {
        this.overhangGrade = overhangGrade;
    }

    public String getSlabGrade() {
        return slabGrade;
    }

    public void setSlabGrade(String slabGrade) {
        this.slabGrade = slabGrade;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    // FOR ALL THE HEIGHT WEIGHT STUFF MAKE SURE TO ROUND TO INTEGERS LATER
    /*
     * 
     * 
     * 
     * AELERT
     * 
     * ALERTTTTTT
     */
    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
        if (heightCm != null) {
            this.heightIn = heightCm / 2.54; // Convert cm to inches
        }
    }

    public Double getHeightIn() {
        return heightIn;
    }

    public void setHeightIn(Double heightIn) {
        this.heightIn = heightIn;
        if (heightIn != null) {
            this.heightCm = heightIn * 2.54; // Convert inches to cm
        }
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
        if (weightKg != null) {
            this.weightLb = weightKg * 2.20462; // Convert kg to lbs
        }
    }

    public Double getWeightLb() {
        return weightLb;
    }

    public void setWeightLb(Double weightLb) {
        this.weightLb = weightLb;
        if (weightLb != null) {
            this.weightKg = weightLb / 2.20462; // Convert lbs to kg
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
