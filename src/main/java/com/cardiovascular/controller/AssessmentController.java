package com.cardiovascular.controller;

import com.cardiovascular.dto.AssessmentResponse;
import com.cardiovascular.dto.AssessmentSummaryResponse;
import com.cardiovascular.dto.CreateAssessmentRequest;
import com.cardiovascular.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/patients/{patientId}/assessments")
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse createAssessment(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateAssessmentRequest request) {
        return assessmentService.createAssessment(patientId, request);
    }

    @GetMapping("/assessments/{id}")
    public AssessmentResponse getAssessment(@PathVariable Long id) {
        return assessmentService.getAssessment(id);
    }

    @GetMapping("/assessments")
    public List<AssessmentSummaryResponse> listAssessments() {
        return assessmentService.getAssessmentHistory();
    }

    @GetMapping("/assessments/recent")
    public List<AssessmentSummaryResponse> recentAssessments() {
        return assessmentService.getRecentAssessments();
    }
}
