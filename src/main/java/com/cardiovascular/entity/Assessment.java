package com.cardiovascular.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private int sex;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private int hyperlipidemia;

    @Column(nullable = false)
    private int smoker;

    @Column(nullable = false)
    private int diabetes;

    @Column(nullable = false)
    private int obesity;

    @Column(nullable = false)
    private int hypertension;

    @Column(nullable = false)
    private double predictedProbability;

    @Column(nullable = false)
    private int predictedClass;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeatureContributionRecord> contributions = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHyperlipidemia() {
        return hyperlipidemia;
    }

    public void setHyperlipidemia(int hyperlipidemia) {
        this.hyperlipidemia = hyperlipidemia;
    }

    public int getSmoker() {
        return smoker;
    }

    public void setSmoker(int smoker) {
        this.smoker = smoker;
    }

    public int getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(int diabetes) {
        this.diabetes = diabetes;
    }

    public int getObesity() {
        return obesity;
    }

    public void setObesity(int obesity) {
        this.obesity = obesity;
    }

    public int getHypertension() {
        return hypertension;
    }

    public void setHypertension(int hypertension) {
        this.hypertension = hypertension;
    }

    public double getPredictedProbability() {
        return predictedProbability;
    }

    public void setPredictedProbability(double predictedProbability) {
        this.predictedProbability = predictedProbability;
    }

    public int getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(int predictedClass) {
        this.predictedClass = predictedClass;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<FeatureContributionRecord> getContributions() {
        return contributions;
    }

    public void addContribution(FeatureContributionRecord contribution) {
        contribution.setAssessment(this);
        contributions.add(contribution);
    }
}
