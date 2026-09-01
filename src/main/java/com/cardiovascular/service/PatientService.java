package com.cardiovascular.service;

import com.cardiovascular.dto.CreatePatientRequest;
import com.cardiovascular.dto.PatientResponse;
import com.cardiovascular.entity.Patient;
import com.cardiovascular.exception.DuplicatePatientCodeException;
import com.cardiovascular.exception.ResourceNotFoundException;
import com.cardiovascular.repository.AssessmentRepository;
import com.cardiovascular.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AssessmentRepository assessmentRepository;
    private final PatientMapper mapper;

    public PatientService(
            PatientRepository patientRepository,
            AssessmentRepository assessmentRepository,
            PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.assessmentRepository = assessmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        String patientCode = normalize(request.patientCode());
        if (patientCode == null) {
            patientCode = nextPatientCode();
        }
        if (patientRepository.existsByPatientCode(patientCode)) {
            throw new DuplicatePatientCodeException(patientCode);
        }

        Patient patient = new Patient();
        patient.setPatientCode(patientCode);
        patient.setFirstName(normalize(request.firstName()));
        patient.setLastName(normalize(request.lastName()));

        Patient saved = patientRepository.save(patient);
        return mapper.toPatientResponse(saved, assessmentRepository.findFirstByPatientIdOrderByCreatedAtDesc(saved.getId()));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String query) {
        String normalizedQuery = normalize(query);
        List<Patient> patients = normalizedQuery == null
                ? patientRepository.findAllByOrderByPatientCodeAsc()
                : patientRepository.findByPatientCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByPatientCodeAsc(
                        normalizedQuery,
                        normalizedQuery,
                        normalizedQuery
                );

        return patients
                .stream()
                .map(patient -> mapper.toPatientResponse(
                        patient,
                        assessmentRepository.findFirstByPatientIdOrderByCreatedAtDesc(patient.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatient(Long id) {
        Patient patient = getPatientEntity(id);
        return mapper.toPatientResponse(patient, assessmentRepository.findFirstByPatientIdOrderByCreatedAtDesc(id));
    }

    @Transactional(readOnly = true)
    public Patient getPatientEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private synchronized String nextPatientCode() {
        long next = patientRepository.count() + 1;
        String code;
        do {
            code = String.format("PAT-%06d", next++);
        } while (patientRepository.existsByPatientCode(code));
        return code;
    }
}
