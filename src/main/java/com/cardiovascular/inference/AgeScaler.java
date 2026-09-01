package com.cardiovascular.inference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AgeScaler {

    private final double mean;
    private final double standardDeviation;

    public AgeScaler(double mean, double standardDeviation) {
        if (standardDeviation == 0.0) {
            throw new IllegalArgumentException("Age standard deviation cannot be zero.");
        }
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public static AgeScaler load(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.size() < 2) {
                throw new IllegalStateException("Age scaler file must contain mean and standard deviation.");
            }
            return new AgeScaler(
                    Double.parseDouble(lines.get(0).trim()),
                    Double.parseDouble(lines.get(1).trim())
            );
        } catch (IOException | NumberFormatException ex) {
            throw new IllegalStateException("Failed to load age scaler from " + path, ex);
        }
    }

    public float standardize(float rawAge) {
        return (float) ((rawAge - mean) / standardDeviation);
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
