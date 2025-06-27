package com.allan.climberanalyzer.analyzer.DTOClass;

public class InputNumbers {
    private int fingerStrength;
    private int hangTime;
    private int edgeSize;
    private int pullingStrength;
    private int reps;
    private int bodyweight;
    private int overHangGrade;
    private int verticalGrade;
    private int slabGrade;

    public int getReps() {
        return this.reps;
    }

    public int getHangTime() {
        return this.hangTime;
    }

    public int getEdgeSize() {
        return this.edgeSize;
    }

    public int getOverHangGrade() {
        return this.overHangGrade;
    }

    public int getVerticalGrade() {
        return this.verticalGrade;
    }

    public int getSlabGrade() {
        return this.slabGrade;
    }

    public int getFingerStrength() {
        return this.fingerStrength;
    }

    public int getPullingStrength() {
        return this.pullingStrength;
    }

    public int getBodyweight() {
        return this.bodyweight;
    }

    public void setOverHangGrade(int overHangGrade) {
        this.overHangGrade = overHangGrade;
    }

    public void setVerticalGrade(int verticalGrade) {
        this.verticalGrade = verticalGrade;
    }

    public void setSlabGrade(int slabGrade) {
        this.slabGrade = slabGrade;
    }

    public void setFingerStrength(int a) {
        this.fingerStrength = a;
    }

    public void setHangTime(int hangTime) {
        this.hangTime = hangTime;
    }

    public void setEdgeSize(int edgeSize) {
        this.edgeSize = edgeSize;
    }

    public void setPullingStrength(int a) {
        this.pullingStrength = a;
    }

    public void setBodyweight(int bodyweight) {
        this.bodyweight = bodyweight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}
