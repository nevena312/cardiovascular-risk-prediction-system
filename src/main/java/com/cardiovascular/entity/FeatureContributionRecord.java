package com.cardiovascular.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_contribution_records")
public class FeatureContributionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(nullable = false, length = 64)
    private String featureName;

    @Column(nullable = false)
    private double originalValue;

    @Column(nullable = false)
    private double referenceValue;

    @Column(nullable = false)
    private double originalProbability;

    @Column(nullable = false)
    private double referenceProbability;

    @Column(nullable = false)
    private double contribution;

    @Column(nullable = false)
    private double absoluteContribution;

    @Column(nullable = false)
    private int rank;

    public Long getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
    }

    public double getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(double referenceValue) {
        this.referenceValue = referenceValue;
    }

    public double getOriginalProbability() {
        return originalProbability;
    }

    public void setOriginalProbability(double originalProbability) {
        this.originalProbability = originalProbability;
    }

    public double getReferenceProbability() {
        return referenceProbability;
    }

    public void setReferenceProbability(double referenceProbability) {
        this.referenceProbability = referenceProbability;
    }

    public double getContribution() {
        return contribution;
    }

    public void setContribution(double contribution) {
        this.contribution = contribution;
    }

    public double getAbsoluteContribution() {
        return absoluteContribution;
    }

    public void setAbsoluteContribution(double absoluteContribution) {
        this.absoluteContribution = absoluteContribution;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
