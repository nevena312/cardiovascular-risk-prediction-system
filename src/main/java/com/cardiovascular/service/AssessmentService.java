package com.cardiovascular.service;

import com.cardiovascular.dto.AssessmentResponse;
import com.cardiovascular.dto.AssessmentSummaryResponse;
import com.cardiovascular.dto.CreateAssessmentRequest;
import com.cardiovascular.entity.Assessment;
import com.cardiovascular.entity.FeatureContributionRecord;
import com.cardiovascular.entity.Patient;
import com.cardiovascular.exception.ResourceNotFoundException;
import com.cardiovascular.explainability.FeatureContribution;
import com.cardiovascular.inference.DeepNettsInferenceService;
import com.cardiovascular.inference.ModelInput;
import com.cardiovascular.inference.PredictionResult;
import com.cardiovascular.repository.AssessmentRepository;
import com.cardiovascular.repository.FeatureContributionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssessmentService {

    private final PatientService patientService;
    private final AssessmentRepository assessmentRepository;
    private final FeatureContributionRecordRepository contributionRepository;
    private final DeepNettsInferenceService inferenceService;
    private final PatientMapper mapper;

    public AssessmentService(
            PatientService patientService,
            AssessmentRepository assessmentRepository,
            FeatureContributionRecordRepository contributionRepository,
            DeepNettsInferenceService inferenceService,
            PatientMapper mapper) {
        this.patientService = patientService;
        this.assessmentRepository = assessmentRepository;
        this.contributionRepository = contributionRepository;
        this.inferenceService = inferenceService;
        this.mapper = mapper;
    }

    @Transactional
    public AssessmentResponse createAssessment(Long patientId, CreateAssessmentRequest request) {
        Patient patient = patientService.getPatientEntity(patientId);
        ModelInput input = toModelInput(request);

        PredictionResult prediction = inferenceService.predict(input);
        List<FeatureContribution> contributions = inferenceService.explain(input);

        Assessment assessment = new Assessment();
        assessment.setPatient(patient);
        assessment.setSex(input.sex());
        assessment.setAge(input.age());
        assessment.setHyperlipidemia(input.hyperlipidemia());
        assessment.setSmoker(input.smoker());
        assessment.setDiabetes(input.diabetes());
        assessment.setObesity(input.obesity());
        assessment.setHypertension(input.hypertension());
        assessment.setPredictedProbability(prediction.predictedProbability());
        assessment.setPredictedClass(prediction.predictedClass());

        for (int i = 0; i < contributions.size(); i++) {
            assessment.addContribution(toRecord(contributions.get(i), i + 1));
        }

        Assessment saved = assessmentRepository.save(assessment);
        return toAssessmentResponse(saved);
    }

    @Transactional(readOnly = true)
    public AssessmentResponse getAssessment(Long id) {
        Assessment assessment = assessmentRepository.findDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + id));
        return toAssessmentResponse(assessment);
    }

    @Transactional(readOnly = true)
    public List<AssessmentSummaryResponse> getAssessmentHistory() {
        return assessmentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toAssessmentSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssessmentSummaryResponse> getPatientHistory(Long patientId) {
        patientService.getPatientEntity(patientId);
        return assessmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(mapper::toAssessmentSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssessmentSummaryResponse> getRecentAssessments() {
        return assessmentRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toAssessmentSummary)
                .toList();
    }

    private AssessmentResponse toAssessmentResponse(Assessment assessment) {
        List<FeatureContributionRecord> records = assessment.getContributions().isEmpty()
                ? contributionRepository.findByAssessmentIdOrderByRankAsc(assessment.getId())
                : assessment.getContributions();
        return new AssessmentResponse(
                assessment.getId(),
                mapper.toPatientBrief(assessment.getPatient()),
                assessment.getSex(),
                assessment.getAge(),
                assessment.getHyperlipidemia(),
                assessment.getSmoker(),
                assessment.getDiabetes(),
                assessment.getObesity(),
                assessment.getHypertension(),
                assessment.getPredictedProbability(),
                assessment.getPredictedClass(),
                assessment.getCreatedAt(),
                mapper.toContributionResponses(records)
        );
    }

    private FeatureContributionRecord toRecord(FeatureContribution contribution, int rank) {
        FeatureContributionRecord record = new FeatureContributionRecord();
        record.setFeatureName(contribution.getFeatureName());
        record.setOriginalValue(contribution.getOriginalValue());
        record.setReferenceValue(contribution.getReferenceValue());
        record.setOriginalProbability(contribution.getOriginalProbability());
        record.setReferenceProbability(contribution.getReferenceProbability());
        record.setContribution(contribution.getContribution());
        record.setAbsoluteContribution(contribution.getAbsoluteContribution());
        record.setRank(rank);
        return record;
    }

    private ModelInput toModelInput(CreateAssessmentRequest request) {
        return new ModelInput(
                request.sex(),
                request.age(),
                request.hyperlipidemia(),
                request.smoker(),
                request.diabetes(),
                request.obesity(),
                request.hypertension()
        );
    }
}
