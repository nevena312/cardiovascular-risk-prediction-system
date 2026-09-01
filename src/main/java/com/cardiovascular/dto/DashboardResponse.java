package com.cardiovascular.dto;

import java.util.List;

public record DashboardResponse(
        long totalPatients,
        long totalAssessments,
        long latestPositivePatients,
        double latestPositivePercentage,
        double latestAverageProbability,
        List<ChartPointResponse> classificationDistribution,
        List<ChartPointResponse> probabilityBuckets,
        List<ChartPointResponse> assessmentsOverTime,
        List<AssessmentSummaryResponse> recentAssessments
) {
}
