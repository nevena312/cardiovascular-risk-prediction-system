package com.cardiovascular.service;

import com.cardiovascular.dto.ChartPointResponse;
import com.cardiovascular.dto.DashboardResponse;
import com.cardiovascular.entity.Assessment;
import com.cardiovascular.repository.AssessmentRepository;
import com.cardiovascular.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final AssessmentRepository assessmentRepository;
    private final PatientMapper mapper;

    public DashboardService(
            PatientRepository patientRepository,
            AssessmentRepository assessmentRepository,
            PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.assessmentRepository = assessmentRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long totalPatients = patientRepository.count();
        long totalAssessments = assessmentRepository.count();
        List<Assessment> latestAssessments = assessmentRepository.findLatestAssessmentForEachPatient();

        long latestPositive = latestAssessments.stream()
                .filter(assessment -> assessment.getPredictedClass() == 1)
                .count();

        double positivePercentage = latestAssessments.isEmpty()
                ? 0.0
                : latestPositive * 100.0 / latestAssessments.size();

        double averageProbability = latestAssessments.stream()
                .mapToDouble(Assessment::getPredictedProbability)
                .average()
                .orElse(0.0);

        return new DashboardResponse(
                totalPatients,
                totalAssessments,
                latestPositive,
                positivePercentage,
                averageProbability,
                classificationDistribution(latestAssessments),
                probabilityBuckets(latestAssessments),
                assessmentsOverTime(),
                assessmentRepository.findTop10ByOrderByCreatedAtDesc()
                        .stream()
                        .map(mapper::toAssessmentSummary)
                        .toList()
        );
    }

    private List<ChartPointResponse> classificationDistribution(List<Assessment> latestAssessments) {
        long positive = latestAssessments.stream().filter(a -> a.getPredictedClass() == 1).count();
        long negative = latestAssessments.size() - positive;
        return List.of(
                new ChartPointResponse("Negativna klasifikacija", negative),
                new ChartPointResponse("Pozitivna klasifikacija", positive)
        );
    }

    private List<ChartPointResponse> probabilityBuckets(List<Assessment> latestAssessments) {
        int[] buckets = new int[5];
        for (Assessment assessment : latestAssessments) {
            int index = Math.min((int) Math.floor(assessment.getPredictedProbability() * 5), 4);
            buckets[index]++;
        }
        return List.of(
                new ChartPointResponse("0-20%", buckets[0]),
                new ChartPointResponse("20-40%", buckets[1]),
                new ChartPointResponse("40-60%", buckets[2]),
                new ChartPointResponse("60-80%", buckets[3]),
                new ChartPointResponse("80-100%", buckets[4])
        );
    }

    private List<ChartPointResponse> assessmentsOverTime() {
        Map<LocalDate, Long> byDate = new TreeMap<>();
        assessmentRepository.findAll().forEach(assessment ->
                byDate.merge(assessment.getCreatedAt().toLocalDate(), 1L, Long::sum)
        );
        return byDate.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> new ChartPointResponse(entry.getKey().toString(), entry.getValue()))
                .toList();
    }
}
