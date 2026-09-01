package com.cardiovascular.service;

import com.cardiovascular.dto.CreatePatientRequest;
import com.cardiovascular.dto.PatientResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:patient-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Test
    void returnsEmptyListWhenNoSearchAndNoPatientsExist() {
        List<PatientResponse> patients = patientService.searchPatients(null);

        assertThat(patients).isEmpty();
    }

    @Test
    void searchesByPatientCodeAndName() {
        PatientResponse first = patientService.createPatient(
                new CreatePatientRequest("PAT-ABC001", "Milan", "Petrovic")
        );
        patientService.createPatient(
                new CreatePatientRequest("PAT-XYZ002", "Ana", "Jovanovic")
        );

        assertThat(patientService.searchPatients("abc"))
                .extracting(PatientResponse::id)
                .containsExactly(first.id());

        assertThat(patientService.searchPatients("milan"))
                .extracting(PatientResponse::id)
                .containsExactly(first.id());

        assertThat(patientService.searchPatients("petrovic"))
                .extracting(PatientResponse::id)
                .containsExactly(first.id());
    }
}
