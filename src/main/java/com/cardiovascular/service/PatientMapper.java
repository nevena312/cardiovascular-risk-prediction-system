package com.cardiovascular.service;

import com.cardiovascular.dto.AssessmentSummaryResponse;
import com.cardiovascular.dto.FeatureContributionResponse;
import com.cardiovascular.dto.PatientBriefResponse;
import com.cardiovascular.dto.PatientResponse;
import com.cardiovascular.entity.Assessment;
import com.cardiovascular.entity.FeatureContributionRecord;
import com.cardiovascular.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class PatientMapper {

    public PatientResponse toPatientResponse(Patient patient, Optional<Assessment> latestAssessment) {
        return new PatientResponse(
                patient.getId(),
                patient.getPatientCode(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getCreatedAt(),
                latestAssessment.map(this::toAssessmentSummary).orElse(null)
        );
    }

    public PatientBriefResponse toPatientBrief(Patient patient) {
        return new PatientBriefResponse(
                patient.getId(),
                patient.getPatientCode(),
                patient.getFirstName(),
                patient.getLastName()
        );
    }

    public AssessmentSummaryResponse toAssessmentSummary(Assessment assessment) {
        Patient patient = assessment.getPatient();
        return new AssessmentSummaryResponse(
                assessment.getId(),
                patient.getId(),
                patient.getPatientCode(),
                patientName(patient),
                assessment.getSex(),
                assessment.getAge(),
                assessment.getHyperlipidemia(),
                assessment.getSmoker(),
                assessment.getDiabetes(),
                assessment.getObesity(),
                assessment.getHypertension(),
                assessment.getPredictedProbability(),
                assessment.getPredictedClass(),
                assessment.getCreatedAt()
        );
    }

    public List<FeatureContributionResponse> toContributionResponses(List<FeatureContributionRecord> records) {
        return records.stream()
                .sorted(Comparator.comparingInt(FeatureContributionRecord::getRank))
                .map(record -> new FeatureContributionResponse(
                        record.getFeatureName(),
                        record.getOriginalValue(),
                        record.getReferenceValue(),
                        record.getOriginalProbability(),
                        record.getReferenceProbability(),
                        record.getContribution(),
                        record.getAbsoluteContribution(),
                        record.getRank()
                ))
                .toList();
    }

    private String patientName(Patient patient) {
        String firstName = patient.getFirstName() == null ? "" : patient.getFirstName();
        String lastName = patient.getLastName() == null ? "" : patient.getLastName();
        String name = (firstName + " " + lastName).trim();
        return name.isBlank() ? null : name;
    }
}
