package com.cardiovascular.dto;

public record FeatureContributionResponse(
        String featureName,
        double originalValue,
        double referenceValue,
        double originalProbability,
        double referenceProbability,
        double contribution,
        double absoluteContribution,
        int rank
) {
}
