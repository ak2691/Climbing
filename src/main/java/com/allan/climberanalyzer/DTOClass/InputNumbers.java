package com.allan.climberanalyzer.DTOClass;

public class InputNumbers {
    private int fingerStrength;
    private int pullingStrength;
    private int bodyweight;

    public int getFingerStrength() {
        return this.fingerStrength;
    }

    public int getPullingStrength() {
        return this.pullingStrength;
    }

    public int getBodyweight() {
        return this.bodyweight;
    }

    public void setFingerStrength(int a) {
        this.fingerStrength = a;
    }

    public void setPullingStrength(int a) {
        this.pullingStrength = a;
    }

    public void setBodyweight(int bodyweight) {
        this.bodyweight = bodyweight;
    }
}
