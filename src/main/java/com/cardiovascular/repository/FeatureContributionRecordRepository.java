package com.cardiovascular.repository;

import com.cardiovascular.entity.FeatureContributionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatureContributionRecordRepository extends JpaRepository<FeatureContributionRecord, Long> {

    List<FeatureContributionRecord> findByAssessmentIdOrderByRankAsc(Long assessmentId);
}
