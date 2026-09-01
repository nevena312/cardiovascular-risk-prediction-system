package com.cardiovascular.evaluation;

public class Prediction {

    private final int actual;
    private final double probability;

    public Prediction(int actual, double probability) {
        this.actual = actual;
        this.probability = probability;
    }

    public int getActual() {
        return actual;
    }

    public double getProbability() {
        return probability;
    }
}