package com.cardiovascular.inference;

import com.cardiovascular.explainability.FeatureContribution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:inference-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DeepNettsInferenceServiceTest {

    @Autowired
    private DeepNettsInferenceService inferenceService;

    @Test
    void loadsSavedAgeScaler() {
        AgeScaler scaler = inferenceService.getAgeScaler();

        assertThat(scaler.getMean()).isCloseTo(65.73952641165756, within(0.000001));
        assertThat(scaler.getStandardDeviation()).isCloseTo(10.156852943037881, within(0.000001));
        assertThat(scaler.standardize(51)).isCloseTo(-1.451189f, within(0.0001f));
    }

    @Test
    void reproducesKnownValidatedPatientPredictionAndExplanation() {
        ModelInput input = new ModelInput(1, 51, 1, 0, 1, 1, 1);

        PredictionResult result = inferenceService.predict(input);
        List<FeatureContribution> contributions = inferenceService.explain(input);

        assertThat(result.predictedProbability()).isCloseTo(0.881591, within(0.0005));
        assertThat(result.predictedClass()).isEqualTo(1);
        assertThat(contributions).hasSize(7);
        assertThat(contributions.get(0).getFeatureName()).isEqualTo("Hyperlipidemia");
        assertThat(contributions.get(0).getContribution()).isCloseTo(0.4591, within(0.001));
        assertThat(contributions.get(1).getFeatureName()).isEqualTo("Diabetes");
        assertThat(contributions.get(1).getContribution()).isCloseTo(0.1367, within(0.001));
        assertThat(contributions.get(2).getFeatureName()).isEqualTo("Hypertension");
        assertThat(contributions.get(2).getContribution()).isCloseTo(0.1015, within(0.001));
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }

    private static org.assertj.core.data.Offset<Float> within(float value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
