package com.cardiovascular;

import com.cardiovascular.data.AgeStandardizer;
import com.cardiovascular.data.CsvDatasetLoader;
import com.cardiovascular.data.DeepNettsDatasetConverter;
import com.cardiovascular.data.PatientSample;

import com.cardiovascular.evaluation.BinaryClassificationResult;
import com.cardiovascular.evaluation.DeepNettsEvaluator;

import com.cardiovascular.model.DeepNettsModelFactory;
import com.cardiovascular.model.NeuralNetworkConfiguration;

import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.cardiovascular.explainability.DeepNettsExplainer;
import com.cardiovascular.explainability.FeatureContribution;
import com.cardiovascular.explainability.DeepNettsExplanationTester;

public class Main {

    public static void main(String[] args)
            throws Exception {

        /*
         * ============================================================
         * 1. LOAD ORIGINAL TRAIN AND TEST DATA
         * ============================================================
         */

        CsvDatasetLoader loader =
                new CsvDatasetLoader();

        List<PatientSample> trainSamples =
                loader.load(
                        "data/cardiovascular_train.csv"
                );

        List<PatientSample> testSamples =
                loader.load(
                        "data/cardiovascular_test.csv"
                );

        System.out.println(
                "Training samples: "
                        + trainSamples.size()
        );

        System.out.println(
                "Test samples: "
                        + testSamples.size()
        );


        /*
         * ============================================================
         * 2. FINAL AGE STANDARDIZATION
         *
         * Scaler is fitted ONLY on the training set.
         * The same mean/std are then applied to the test set.
         * ============================================================
         */

        AgeStandardizer standardizer =
                new AgeStandardizer();

        standardizer.fit(
                trainSamples
        );

        System.out.printf(
                "Final age mean: %.6f%n",
                standardizer.getMean()
        );

        System.out.printf(
                "Final age standard deviation: %.6f%n",
                standardizer.getStandardDeviation()
        );

        standardizer.transform(
                trainSamples
        );

        standardizer.transform(
                testSamples
        );


        /*
         * ============================================================
         * 3. CONVERT TRAIN SET TO DEEPNETTS DATASET
         * ============================================================
         */

        DeepNettsDatasetConverter converter =
                new DeepNettsDatasetConverter();

        TabularDataSet<TabularDataSet.Item>
                trainingDataSet =
                converter.convert(
                        trainSamples
                );


        /*
         * ============================================================
         * 4. LOCKED FINAL CONFIGURATION
         *
         * Selected previously using 5-fold cross-validation:
         *
         * hiddenLayers = [8]
         * learningRate = 0.001
         * epochs = 500
         *
         * CV ROC-AUC = 0.7904 ± 0.0788
         * ============================================================
         */

        NeuralNetworkConfiguration finalConfiguration =
                new NeuralNetworkConfiguration(
                        new int[]{8},
                        0.001f,
                        500
                );

        System.out.println(
                "\nFinal DeepNetts configuration:"
        );

        System.out.println(
                finalConfiguration
        );


        /*
         * ============================================================
         * 5. CREATE FINAL NETWORK
         * ============================================================
         */

        DeepNettsModelFactory modelFactory =
                new DeepNettsModelFactory();

        FeedForwardNetwork finalNetwork =
                modelFactory.createModel(
                        finalConfiguration,
                        42L
                );

        System.out.println(
                "\nFinal network:"
        );

        System.out.println(
                finalNetwork
        );


        /*
         * ============================================================
         * 6. TRAIN FINAL NETWORK ON ALL 549 TRAINING PATIENTS
         * ============================================================
         */

        BackpropagationTrainer finalTrainer =
                finalNetwork.getTrainer();

        finalTrainer
                .setLearningRate(
                        finalConfiguration.learningRate()
                )
                .setMaxEpochs(
                        finalConfiguration.epochs()
                );

        System.out.println(
                "\nTraining final DeepNetts model..."
        );

        finalTrainer.train(
                trainingDataSet
        );

        System.out.println(
                "\nFinal training completed."
        );


        /*
         * ============================================================
         * 7. EVALUATION
         * ============================================================
         */

        DeepNettsEvaluator evaluator =
                new DeepNettsEvaluator();


        /*
         * ------------------------------------------------------------
         * TRAIN RESULTS
         * ------------------------------------------------------------
         */

        BinaryClassificationResult trainResult =
                evaluator.evaluate(
                        finalNetwork,
                        trainSamples,
                        0.5f
                );

        double trainRocAuc =
                evaluator.calculateRocAuc(
                        finalNetwork,
                        trainSamples
                );

        double trainAveragePrecision =
                evaluator.calculateAveragePrecision(
                        finalNetwork,
                        trainSamples
                );

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "FINAL DEEPNETTS TRAIN RESULTS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                trainResult
        );

        System.out.printf(
                "ROC-AUC: %.4f%n",
                trainRocAuc
        );

        System.out.printf(
                "Average Precision: %.4f%n",
                trainAveragePrecision
        );


        /*
         * ------------------------------------------------------------
         * TEST RESULTS
         * ------------------------------------------------------------
         */

        BinaryClassificationResult testResult =
                evaluator.evaluate(
                        finalNetwork,
                        testSamples,
                        0.5f
                );

        double testRocAuc =
                evaluator.calculateRocAuc(
                        finalNetwork,
                        testSamples
                );

        double testAveragePrecision =
                evaluator.calculateAveragePrecision(
                        finalNetwork,
                        testSamples
                );

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "FINAL DEEPNETTS TEST RESULTS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                testResult
        );

        System.out.printf(
                "ROC-AUC: %.4f%n",
                testRocAuc
        );

        System.out.printf(
                "Average Precision: %.4f%n",
                testAveragePrecision
        );


        /*
         * ============================================================
         * 8. SAVE FINAL MODEL AND PREPROCESSING PARAMETERS
         * ============================================================
         */

        Files.createDirectories(
                Path.of("models")
        );

        String modelPath =
                "models/deepnetts_cardiovascular_model.dnet";

        FileIO.writeToFile(
                finalNetwork,
                modelPath
        );


        /*
         * Save age scaler
         */

        try (
                PrintWriter writer =
                        new PrintWriter(
                                "models/age_scaler.txt"
                        )
        ) {

            writer.println(
                    standardizer.getMean()
            );

            writer.println(
                    standardizer
                            .getStandardDeviation()
            );
        }


        /*
         * Save model configuration
         */

        try (
                PrintWriter writer =
                        new PrintWriter(
                                "models/model_config.txt"
                        )
        ) {

            writer.println(
                    "inputFeatures=7"
            );

            writer.println(
                    "hiddenLayers=8"
            );

            writer.println(
                    "learningRate=0.001"
            );

            writer.println(
                    "epochs=500"
            );

            writer.println(
                    "hiddenActivation=RELU"
            );

            writer.println(
                    "outputActivation=SIGMOID"
            );

            writer.println(
                    "loss=CROSS_ENTROPY"
            );

            writer.println(
                    "threshold=0.5"
            );

            writer.println(
                    "randomSeed=42"
            );
        }

        System.out.println(
                "\nModel and preprocessing parameters saved."
        );


        /*
         * ============================================================
         * 9. LOAD SAVED MODEL AND VERIFY PREDICTION
         * ============================================================
         */

        FeedForwardNetwork loadedNetwork =
                FileIO.createFromFile(
                        modelPath,
                        FeedForwardNetwork.class
                );

        PatientSample verificationPatient =
                testSamples.get(0);

        float originalProbability =
                finalNetwork.predict(
                        verificationPatient.getFeatures()
                )[0];

        float loadedProbability =
                loadedNetwork.predict(
                        verificationPatient.getFeatures()
                )[0];

        boolean identical =
                Math.abs(
                        originalProbability
                                - loadedProbability
                ) < 1e-7;


        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "MODEL SAVE/LOAD VERIFICATION"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Patient original index: "
                        + verificationPatient.getOriginalIndex()
        );

        System.out.println(
                "Original probability: "
                        + originalProbability
        );

        System.out.println(
                "Loaded probability:   "
                        + loadedProbability
        );

        System.out.println(
                "Identical: "
                        + identical
        );

        System.out.println();
        System.out.println("========================================");
        System.out.println("DEEPNETTS LOCAL EXPLANATION TEST");
        System.out.println("========================================");

        DeepNettsExplainer explainer = new DeepNettsExplainer(
                loadedNetwork,
                standardizer.getMean(),
                standardizer.getStandardDeviation()
        );

        List<PatientSample> rawTestSamples =
                loader.load("data/cardiovascular_test.csv");

        PatientSample explanationPatient = rawTestSamples.get(0);

        float[] rawPatientFeatures =
                explanationPatient.getFeatures().clone();

        double explanationProbability =
                explainer.predictRaw(rawPatientFeatures);

        System.out.println(
                "Patient original index: "
                        + explanationPatient.getOriginalIndex()
        );

        System.out.println(
                "Actual class: "
                        + explanationPatient.getLabel()
        );

        System.out.println(
                "Predicted probability: "
                        + String.format("%.6f", explanationProbability)
        );

        System.out.println(
                "Predicted class: "
                        + (explanationProbability >= 0.5 ? 1 : 0)
        );

        System.out.println();
        System.out.println("Raw patient features:");

        System.out.println(
                java.util.Arrays.toString(rawPatientFeatures)
        );

        System.out.println();
        System.out.println("Local feature contributions:");

        List<FeatureContribution> contributions =
                explainer.explain(rawPatientFeatures);

        for (int i = 0; i < contributions.size(); i++) {

            FeatureContribution contribution =
                    contributions.get(i);

            System.out.printf(
                    "%d. %s%n",
                    i + 1,
                    contribution
            );
        }


        DeepNettsExplanationTester explanationTester =
                new DeepNettsExplanationTester(
                        explainer
                );

        explanationTester.testRepresentativeCases(
                rawTestSamples
        );
    }
}