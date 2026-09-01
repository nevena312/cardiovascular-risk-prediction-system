package com.cardiovascular.inference;

import com.cardiovascular.explainability.DeepNettsExplainer;
import com.cardiovascular.explainability.FeatureContribution;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class DeepNettsInferenceService {

    private final Path modelPath;
    private final Path scalerPath;
    private final double threshold;

    private FeedForwardNetwork network;
    private AgeScaler ageScaler;
    private DeepNettsExplainer explainer;

    public DeepNettsInferenceService(
            @Value("${cardiovascular.model.path}") String modelPath,
            @Value("${cardiovascular.scaler.path}") String scalerPath,
            @Value("${cardiovascular.prediction.threshold}") double threshold) {
        this.modelPath = Path.of(modelPath);
        this.scalerPath = Path.of(scalerPath);
        this.threshold = threshold;
    }

    @PostConstruct
    public void initialize() {
        try {
            network = FileIO.createFromFile(modelPath.toString(), FeedForwardNetwork.class);
            ageScaler = AgeScaler.load(scalerPath);
            explainer = new DeepNettsExplainer(
                    network,
                    ageScaler.getMean(),
                    ageScaler.getStandardDeviation()
            );
        } catch (IOException | ClassNotFoundException | RuntimeException ex) {
            throw new IllegalStateException("Failed to initialize saved DeepNetts model.", ex);
        }
    }

    public synchronized PredictionResult predict(ModelInput input) {
        double probability = explainer.predictRaw(input.toRawFeatureArray());
        return new PredictionResult(probability, probability >= threshold ? 1 : 0);
    }

    public synchronized List<FeatureContribution> explain(ModelInput input) {
        return explainer.explain(input.toRawFeatureArray());
    }

    public AgeScaler getAgeScaler() {
        return ageScaler;
    }
}
