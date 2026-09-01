package com.cardiovascular.evaluation;

import com.cardiovascular.data.PatientSample;
import deepnetts.net.FeedForwardNetwork;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;

public class DeepNettsEvaluator {

    public BinaryClassificationResult evaluate(
            FeedForwardNetwork network,
            List<PatientSample> samples,
            float threshold
    ) {

        int tn = 0;
        int fp = 0;
        int fn = 0;
        int tp = 0;

        for (PatientSample sample : samples) {

            float probability =
                    network.predict(
                            sample.getFeatures()
                    )[0];

            int prediction =
                    probability >= threshold
                            ? 1
                            : 0;

            int actual =
                    sample.getLabel();

            if (actual == 0 && prediction == 0) {
                tn++;
            } else if (
                    actual == 0 &&
                            prediction == 1
            ) {
                fp++;
            } else if (
                    actual == 1 &&
                            prediction == 0
            ) {
                fn++;
            } else if (
                    actual == 1 &&
                            prediction == 1
            ) {
                tp++;
            }
        }

        double accuracy =
                (double) (tp + tn)
                        / samples.size();

        double precision =
                tp + fp == 0
                        ? 0.0
                        : (double) tp / (tp + fp);

        double sensitivity =
                tp + fn == 0
                        ? 0.0
                        : (double) tp / (tp + fn);

        double specificity =
                tn + fp == 0
                        ? 0.0
                        : (double) tn / (tn + fp);

        double balancedAccuracy =
                (sensitivity + specificity) / 2.0;

        double f1 =
                precision + sensitivity == 0
                        ? 0.0
                        : 2.0
                        * precision
                        * sensitivity
                        / (
                        precision
                                + sensitivity
                );

        return new BinaryClassificationResult(
                tn,
                fp,
                fn,
                tp,
                accuracy,
                balancedAccuracy,
                precision,
                sensitivity,
                specificity,
                f1
        );
    }

    public double calculateRocAuc(
            FeedForwardNetwork network,
            List<PatientSample> samples
    ) {

        List<Prediction> predictions =
                new ArrayList<>();

        for (PatientSample sample : samples) {

            double probability =
                    network.predict(
                            sample.getFeatures()
                    )[0];

            predictions.add(
                    new Prediction(
                            sample.getLabel(),
                            probability
                    )
            );
        }

        predictions.sort(
                Comparator.comparingDouble(
                        Prediction::getProbability
                )
        );

        int positiveCount = 0;
        int negativeCount = 0;

        for (Prediction prediction : predictions) {
            if (prediction.getActual() == 1) {
                positiveCount++;
            } else {
                negativeCount++;
            }
        }

        double rankSumPositive = 0.0;

        int i = 0;

        while (i < predictions.size()) {

            int j = i;

            double probability =
                    predictions.get(i)
                            .getProbability();

            while (
                    j + 1 < predictions.size()
                            &&
                            Double.compare(
                                    predictions.get(j + 1)
                                            .getProbability(),
                                    probability
                            ) == 0
            ) {
                j++;
            }

            double averageRank =
                    ((i + 1) + (j + 1)) / 2.0;

            for (int k = i; k <= j; k++) {

                if (
                        predictions.get(k)
                                .getActual() == 1
                ) {
                    rankSumPositive += averageRank;
                }
            }

            i = j + 1;
        }

        return (
                rankSumPositive
                        - (
                        (double) positiveCount
                                * (positiveCount + 1)
                                / 2.0
                )
        ) / (
                (double) positiveCount
                        * negativeCount
        );
    }

    public double calculateAveragePrecision(
            FeedForwardNetwork network,
            List<PatientSample> samples
    ) {

        List<Prediction> predictions =
                new ArrayList<>();

        for (PatientSample sample : samples) {

            double probability =
                    network.predict(
                            sample.getFeatures()
                    )[0];

            predictions.add(
                    new Prediction(
                            sample.getLabel(),
                            probability
                    )
            );
        }

        predictions.sort(
                Comparator.comparingDouble(
                        Prediction::getProbability
                ).reversed()
        );

        int totalPositive = 0;

        for (Prediction prediction : predictions) {
            if (prediction.getActual() == 1) {
                totalPositive++;
            }
        }

        int truePositive = 0;
        int falsePositive = 0;

        double previousRecall = 0.0;
        double averagePrecision = 0.0;

        for (Prediction prediction : predictions) {

            if (prediction.getActual() == 1) {
                truePositive++;
            } else {
                falsePositive++;
            }

            double precision =
                    (double) truePositive
                            / (truePositive + falsePositive);

            double recall =
                    (double) truePositive
                            / totalPositive;

            if (prediction.getActual() == 1) {

                averagePrecision +=
                        (recall - previousRecall)
                                * precision;

                previousRecall = recall;
            }
        }

        return averagePrecision;
    }
}