package com.cardiovascular.explainability;

import deepnetts.net.FeedForwardNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeepNettsExplainer {

    private static final int AGE_INDEX = 1;

    private static final String[] FEATURE_NAMES = {
            "Sex",
            "Age",
            "Hyperlipidemia",
            "Smoker",
            "Diabetes",
            "Obesity",
            "Hypertension"
    };

    private final FeedForwardNetwork network;
    private final double ageMean;
    private final double ageStandardDeviation;

    public DeepNettsExplainer(
            FeedForwardNetwork network,
            double ageMean,
            double ageStandardDeviation) {

        this.network = network;
        this.ageMean = ageMean;
        this.ageStandardDeviation = ageStandardDeviation;
    }

    /**
     * Calculates local feature contributions for one patient.
     *
     * Binary features:
     * current value is flipped:
     * 0 -> 1
     * 1 -> 0
     *
     * Age:
     * reference value is the mean age from the training set.
     *
     * The contribution is:
     *
     * P(original patient) - P(counterfactual patient)
     *
     * Positive contribution:
     * original value increases the network output compared with reference.
     *
     * Negative contribution:
     * original value decreases the network output compared with reference.
     */
    public List<FeatureContribution> explain(float[] rawFeatures) {

        validateFeatures(rawFeatures);

        double originalProbability = predictRaw(rawFeatures);

        List<FeatureContribution> contributions = new ArrayList<>();

        for (int featureIndex = 0;
             featureIndex < rawFeatures.length;
             featureIndex++) {

            float[] counterfactualFeatures = rawFeatures.clone();

            float originalValue = rawFeatures[featureIndex];
            float referenceValue;

            if (featureIndex == AGE_INDEX) {

                referenceValue = (float) ageMean;
            } else {

                referenceValue = flipBinaryValue(originalValue);
            }

            counterfactualFeatures[featureIndex] = referenceValue;

            double referenceProbability =
                    predictRaw(counterfactualFeatures);

            FeatureContribution contribution =
                    new FeatureContribution(
                            FEATURE_NAMES[featureIndex],
                            originalValue,
                            referenceValue,
                            originalProbability,
                            referenceProbability
                    );

            contributions.add(contribution);
        }

        contributions.sort(
                Comparator.comparingDouble(
                                FeatureContribution::getAbsoluteContribution
                        )
                        .reversed()
        );

        return contributions;
    }

    /**
     * Returns probability predicted by the DeepNetts model.
     *
     * Input features are expected in their ORIGINAL form.
     * Age is standardized here using parameters fitted
     * only on the training dataset.
     */
    public double predictRaw(float[] rawFeatures) {

        validateFeatures(rawFeatures);

        float[] transformedFeatures = rawFeatures.clone();

        transformedFeatures[AGE_INDEX] =
                (float) (
                        (transformedFeatures[AGE_INDEX] - ageMean)
                                / ageStandardDeviation
                );

        return network.predict(transformedFeatures)[0];
    }

    private float flipBinaryValue(float value) {

        if (value == 0.0f) {
            return 1.0f;
        }

        if (value == 1.0f) {
            return 0.0f;
        }

        throw new IllegalArgumentException(
                "Binary feature must have value 0 or 1, but received: "
                        + value
        );
    }

    private void validateFeatures(float[] features) {

        if (features == null) {
            throw new IllegalArgumentException(
                    "Features cannot be null."
            );
        }

        if (features.length != FEATURE_NAMES.length) {
            throw new IllegalArgumentException(
                    "Expected 7 features, but received: "
                            + features.length
            );
        }

        if (ageStandardDeviation == 0.0f) {
            throw new IllegalStateException(
                    "Age standard deviation cannot be zero."
            );
        }
    }
}