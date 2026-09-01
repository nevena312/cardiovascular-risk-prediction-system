package com.cardiovascular.explainability;

public class FeatureContribution {

    private final String featureName;
    private final float originalValue;
    private final float referenceValue;
    private final double originalProbability;
    private final double referenceProbability;
    private final double contribution;

    public FeatureContribution(
            String featureName,
            float originalValue,
            float referenceValue,
            double originalProbability,
            double referenceProbability) {

        this.featureName = featureName;
        this.originalValue = originalValue;
        this.referenceValue = referenceValue;
        this.originalProbability = originalProbability;
        this.referenceProbability = referenceProbability;
        this.contribution = originalProbability - referenceProbability;
    }

    public String getFeatureName() {
        return featureName;
    }

    public float getOriginalValue() {
        return originalValue;
    }

    public float getReferenceValue() {
        return referenceValue;
    }

    public double getOriginalProbability() {
        return originalProbability;
    }

    public double getReferenceProbability() {
        return referenceProbability;
    }

    public double getContribution() {
        return contribution;
    }

    public double getAbsoluteContribution() {
        return Math.abs(contribution);
    }

    @Override
    public String toString() {
        return String.format(
                "%-18s original=%6.3f  reference=%6.3f  " +
                        "P(original)=%.4f  P(reference)=%.4f  contribution=%+.4f",
                featureName,
                originalValue,
                referenceValue,
                originalProbability,
                referenceProbability,
                contribution
        );
    }
}