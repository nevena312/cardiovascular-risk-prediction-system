package com.cardiovascular.service;

import com.cardiovascular.dto.AssessmentResponse;
import com.cardiovascular.dto.CreateAssessmentRequest;
import com.cardiovascular.dto.CreatePatientRequest;
import com.cardiovascular.dto.PatientResponse;
import com.cardiovascular.repository.AssessmentRepository;
import com.cardiovascular.repository.FeatureContributionRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:assessment-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AssessmentServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private FeatureContributionRecordRepository contributionRepository;

    @Test
    void createsPatientAssessmentAndPersistsHistoricalContributions() {
        PatientResponse patient = patientService.createPatient(
                new CreatePatientRequest(null, "Test", "Pacijent")
        );

        AssessmentResponse assessment = assessmentService.createAssessment(
                patient.id(),
                new CreateAssessmentRequest(1, 51, 1, 0, 1, 1, 1)
        );

        assertThat(patient.patientCode()).startsWith("PAT-");
        assertThat(assessment.predictedProbability()).isCloseTo(0.881591, within(0.0005));
        assertThat(assessment.predictedClass()).isEqualTo(1);
        assertThat(assessment.age()).isEqualTo(51);
        assertThat(assessment.contributions()).hasSize(7);
        assertThat(assessmentRepository.findById(assessment.id())).isPresent();
        assertThat(contributionRepository.findByAssessmentIdOrderByRankAsc(assessment.id())).hasSize(7);
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
