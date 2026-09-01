package com.cardiovascular.explainability;

import com.cardiovascular.data.PatientSample;

import java.util.List;

public class DeepNettsExplanationTester {

    private static final double THRESHOLD = 0.5;

    private final DeepNettsExplainer explainer;

    public DeepNettsExplanationTester(
            DeepNettsExplainer explainer) {

        this.explainer = explainer;
    }

    public void testRepresentativeCases(
            List<PatientSample> rawTestSamples) {

        PatientSample truePositive = null;
        PatientSample trueNegative = null;
        PatientSample falsePositive = null;
        PatientSample falseNegative = null;

        for (PatientSample patient : rawTestSamples) {

            double probability =
                    explainer.predictRaw(
                            patient.getFeatures()
                    );

            int predictedClass =
                    probability >= THRESHOLD ? 1 : 0;

            int actualClass =
                    patient.getLabel();

            if (actualClass == 1
                    && predictedClass == 1
                    && truePositive == null) {

                truePositive = patient;

            } else if (actualClass == 0
                    && predictedClass == 0
                    && trueNegative == null) {

                trueNegative = patient;

            } else if (actualClass == 0
                    && predictedClass == 1
                    && falsePositive == null) {

                falsePositive = patient;

            } else if (actualClass == 1
                    && predictedClass == 0
                    && falseNegative == null) {

                falseNegative = patient;
            }

            if (truePositive != null
                    && trueNegative != null
                    && falsePositive != null
                    && falseNegative != null) {

                break;
            }
        }

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "REPRESENTATIVE EXPLANATION CASES"
        );
        System.out.println(
                "========================================"
        );

        printCase(
                "TRUE POSITIVE",
                truePositive
        );

        printCase(
                "TRUE NEGATIVE",
                trueNegative
        );

        printCase(
                "FALSE POSITIVE",
                falsePositive
        );

        printCase(
                "FALSE NEGATIVE",
                falseNegative
        );
    }

    private void printCase(
            String caseName,
            PatientSample patient) {

        System.out.println();
        System.out.println(
                "----------------------------------------"
        );

        System.out.println(caseName);

        System.out.println(
                "----------------------------------------"
        );

        if (patient == null) {

            System.out.println(
                    "No patient found for this category."
            );

            return;
        }

        float[] rawFeatures =
                patient.getFeatures().clone();

        double probability =
                explainer.predictRaw(
                        rawFeatures
                );

        int predictedClass =
                probability >= THRESHOLD ? 1 : 0;

        System.out.println(
                "Patient original index: "
                        + patient.getOriginalIndex()
        );

        System.out.println(
                "Actual class: "
                        + patient.getLabel()
        );

        System.out.printf(
                "Predicted probability: %.6f%n",
                probability
        );

        System.out.println(
                "Predicted class: "
                        + predictedClass
        );

        System.out.println(
                "Raw features: "
                        + java.util.Arrays.toString(
                        rawFeatures
                )
        );

        System.out.println();
        System.out.println(
                "Local feature contributions:"
        );

        List<FeatureContribution> contributions =
                explainer.explain(
                        rawFeatures
                );

        for (int i = 0;
             i < contributions.size();
             i++) {

            System.out.printf(
                    "%d. %s%n",
                    i + 1,
                    contributions.get(i)
            );
        }
    }
}