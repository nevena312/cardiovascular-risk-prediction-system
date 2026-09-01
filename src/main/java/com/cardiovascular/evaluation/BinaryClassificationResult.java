package com.cardiovascular.evaluation;

public class BinaryClassificationResult {

    private final int trueNegative;
    private final int falsePositive;
    private final int falseNegative;
    private final int truePositive;

    private final double accuracy;
    private final double balancedAccuracy;
    private final double precision;
    private final double sensitivity;
    private final double specificity;
    private final double f1;

    public BinaryClassificationResult(
            int trueNegative,
            int falsePositive,
            int falseNegative,
            int truePositive,
            double accuracy,
            double balancedAccuracy,
            double precision,
            double sensitivity,
            double specificity,
            double f1
    ) {
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
        this.truePositive = truePositive;
        this.accuracy = accuracy;
        this.balancedAccuracy = balancedAccuracy;
        this.precision = precision;
        this.sensitivity = sensitivity;
        this.specificity = specificity;
        this.f1 = f1;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }

    public int getTruePositive() {
        return truePositive;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getBalancedAccuracy() {
        return balancedAccuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public double getSpecificity() {
        return specificity;
    }

    public double getF1() {
        return f1;
    }

    @Override
    public String toString() {
        return String.format(
                """
                
                Confusion Matrix
                [[%d, %d],
                 [%d, %d]]
                
                Accuracy:          %.4f
                Balanced Accuracy: %.4f
                Precision:         %.4f
                Sensitivity:       %.4f
                Specificity:       %.4f
                F1:                %.4f
                """,
                trueNegative,
                falsePositive,
                falseNegative,
                truePositive,
                accuracy,
                balancedAccuracy,
                precision,
                sensitivity,
                specificity,
                f1
        );
    }
}