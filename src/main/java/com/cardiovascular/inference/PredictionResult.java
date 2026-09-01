package com.cardiovascular.inference;

public record PredictionResult(
        double predictedProbability,
        int predictedClass
) {
}
