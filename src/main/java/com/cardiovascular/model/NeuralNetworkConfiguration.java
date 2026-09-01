package com.cardiovascular.model;

import java.util.Arrays;

public record NeuralNetworkConfiguration(
        int[] hiddenLayers,
        float learningRate,
        int epochs
) {

    @Override
    public String toString() {
        return "hiddenLayers="
                + Arrays.toString(hiddenLayers)
                + ", learningRate="
                + learningRate
                + ", epochs="
                + epochs;
    }
}