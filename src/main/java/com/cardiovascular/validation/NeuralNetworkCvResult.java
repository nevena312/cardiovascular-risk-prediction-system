package com.cardiovascular.validation;

import com.cardiovascular.model.NeuralNetworkConfiguration;

public class NeuralNetworkCvResult {

    private final NeuralNetworkConfiguration configuration;

    private final double meanRocAuc;
    private final double stdRocAuc;

    private final double meanAccuracy;
    private final double meanBalancedAccuracy;
    private final double meanPrecision;
    private final double meanSensitivity;
    private final double meanSpecificity;
    private final double meanF1;

    public NeuralNetworkCvResult(
            NeuralNetworkConfiguration configuration,
            double meanRocAuc,
            double stdRocAuc,
            double meanAccuracy,
            double meanBalancedAccuracy,
            double meanPrecision,
            double meanSensitivity,
            double meanSpecificity,
            double meanF1
    ) {
        this.configuration = configuration;
        this.meanRocAuc = meanRocAuc;
        this.stdRocAuc = stdRocAuc;
        this.meanAccuracy = meanAccuracy;
        this.meanBalancedAccuracy =
                meanBalancedAccuracy;
        this.meanPrecision = meanPrecision;
        this.meanSensitivity = meanSensitivity;
        this.meanSpecificity = meanSpecificity;
        this.meanF1 = meanF1;
    }

    public NeuralNetworkConfiguration getConfiguration() {
        return configuration;
    }

    public double getMeanRocAuc() {
        return meanRocAuc;
    }

    public double getStdRocAuc() {
        return stdRocAuc;
    }

    @Override
    public String toString() {

        return String.format(
                """
                
                %s
                Mean ROC-AUC:       %.4f
                ROC-AUC SD:         %.4f
                Accuracy:           %.4f
                Balanced Accuracy:  %.4f
                Precision:          %.4f
                Sensitivity:        %.4f
                Specificity:        %.4f
                F1:                 %.4f
                """,
                configuration,
                meanRocAuc,
                stdRocAuc,
                meanAccuracy,
                meanBalancedAccuracy,
                meanPrecision,
                meanSensitivity,
                meanSpecificity,
                meanF1
        );
    }
}