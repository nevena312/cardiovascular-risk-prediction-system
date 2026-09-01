package com.cardiovascular.repository;

import com.cardiovascular.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByPatientCode(String patientCode);

    Optional<Patient> findByPatientCode(String patientCode);

    List<Patient> findAllByOrderByPatientCodeAsc();

    List<Patient> findByPatientCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByPatientCodeAsc(
            String patientCode,
            String firstName,
            String lastName
    );
}
