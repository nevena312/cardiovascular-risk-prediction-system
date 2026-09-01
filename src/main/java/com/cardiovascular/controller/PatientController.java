package com.cardiovascular.controller;

import com.cardiovascular.dto.AssessmentSummaryResponse;
import com.cardiovascular.dto.CreatePatientRequest;
import com.cardiovascular.dto.PatientResponse;
import com.cardiovascular.service.AssessmentService;
import com.cardiovascular.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final AssessmentService assessmentService;

    public PatientController(PatientService patientService, AssessmentService assessmentService) {
        this.patientService = patientService;
        this.assessmentService = assessmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return patientService.createPatient(request);
    }

    @GetMapping
    public List<PatientResponse> listPatients(@RequestParam(required = false) String search) {
        return patientService.searchPatients(search);
    }

    @GetMapping("/{id}")
    public PatientResponse getPatient(@PathVariable Long id) {
        return patientService.getPatient(id);
    }

    @GetMapping("/{id}/assessments")
    public List<AssessmentSummaryResponse> getPatientAssessments(@PathVariable Long id) {
        return assessmentService.getPatientHistory(id);
    }
}
