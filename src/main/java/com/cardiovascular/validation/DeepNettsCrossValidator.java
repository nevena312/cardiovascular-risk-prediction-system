package com.cardiovascular.validation;

import com.cardiovascular.data.AgeStandardizer;
import com.cardiovascular.data.DeepNettsDatasetConverter;
import com.cardiovascular.data.PatientSample;
import com.cardiovascular.data.PatientSampleUtils;
import com.cardiovascular.evaluation.BinaryClassificationResult;
import com.cardiovascular.evaluation.DeepNettsEvaluator;
import com.cardiovascular.model.DeepNettsModelFactory;
import com.cardiovascular.model.NeuralNetworkConfiguration;
import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.train.BackpropagationTrainer;

import java.util.ArrayList;
import java.util.List;

public class DeepNettsCrossValidator {

    private static final float THRESHOLD = 0.5f;

    private final DeepNettsModelFactory modelFactory =
            new DeepNettsModelFactory();

    private final DeepNettsDatasetConverter converter =
            new DeepNettsDatasetConverter();

    private final DeepNettsEvaluator evaluator =
            new DeepNettsEvaluator();

    public NeuralNetworkCvResult evaluate(
            List<List<PatientSample>> folds,
            NeuralNetworkConfiguration configuration
    ) {

        List<Double> rocAucValues =
                new ArrayList<>();

        List<Double> accuracyValues =
                new ArrayList<>();

        List<Double> balancedAccuracyValues =
                new ArrayList<>();

        List<Double> precisionValues =
                new ArrayList<>();

        List<Double> sensitivityValues =
                new ArrayList<>();

        List<Double> specificityValues =
                new ArrayList<>();

        List<Double> f1Values =
                new ArrayList<>();

        for (
                int validationFoldIndex = 0;
                validationFoldIndex < folds.size();
                validationFoldIndex++
        ) {

            List<PatientSample> foldTrain =
                    new ArrayList<>();

            List<PatientSample> foldValidation =
                    new ArrayList<>();

            for (
                    int i = 0;
                    i < folds.size();
                    i++
            ) {

                if (i == validationFoldIndex) {

                    foldValidation.addAll(
                            folds.get(i)
                    );

                } else {

                    foldTrain.addAll(
                            folds.get(i)
                    );
                }
            }
            foldTrain =
                    PatientSampleUtils.deepCopy(
                            foldTrain
                    );

            foldValidation =
                    PatientSampleUtils.deepCopy(
                            foldValidation
                    );

            AgeStandardizer standardizer =
                    new AgeStandardizer();

            standardizer.fit(
                    foldTrain
            );

            standardizer.transform(
                    foldTrain
            );

            standardizer.transform(
                    foldValidation
            );

            TabularDataSet<TabularDataSet.Item>
                    trainingDataSet =
                    converter.convert(
                            foldTrain
                    );

            FeedForwardNetwork network =
                    modelFactory.createModel(
                            configuration,
                            42L + validationFoldIndex
                    );

            BackpropagationTrainer trainer =
                    network.getTrainer();

            trainer
                    .setLearningRate(
                            configuration.learningRate()
                    )
                    .setMaxEpochs(
                            configuration.epochs()
                    );

            trainer.train(
                    trainingDataSet
            );

            BinaryClassificationResult result =
                    evaluator.evaluate(
                            network,
                            foldValidation,
                            THRESHOLD
                    );

            double rocAuc =
                    evaluator.calculateRocAuc(
                            network,
                            foldValidation
                    );

            rocAucValues.add(
                    rocAuc
            );

            accuracyValues.add(
                    result.getAccuracy()
            );

            balancedAccuracyValues.add(
                    result.getBalancedAccuracy()
            );

            precisionValues.add(
                    result.getPrecision()
            );

            sensitivityValues.add(
                    result.getSensitivity()
            );

            specificityValues.add(
                    result.getSpecificity()
            );

            f1Values.add(
                    result.getF1()
            );

            System.out.printf(
                    "  Fold %d ROC-AUC: %.4f%n",
                    validationFoldIndex + 1,
                    rocAuc
            );
        }

        double meanRocAuc =
                mean(rocAucValues);

        double stdRocAuc =
                standardDeviation(
                        rocAucValues,
                        meanRocAuc
                );

        return new NeuralNetworkCvResult(
                configuration,
                meanRocAuc,
                stdRocAuc,
                mean(accuracyValues),
                mean(balancedAccuracyValues),
                mean(precisionValues),
                mean(sensitivityValues),
                mean(specificityValues),
                mean(f1Values)
        );
    }

    private double mean(
            List<Double> values
    ) {

        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

    private double standardDeviation(
            List<Double> values,
            double mean
    ) {

        double sumSquaredDifferences = 0.0;

        for (double value : values) {

            double difference =
                    value - mean;

            sumSquaredDifferences +=
                    difference * difference;
        }

        return Math.sqrt(
                sumSquaredDifferences
                        / values.size()
        );
    }
}