package com.cardiovascular.controller;

import com.cardiovascular.dto.CreatePatientRequest;
import com.cardiovascular.repository.PatientRepository;
import com.cardiovascular.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:patient-controller-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void clearPatients() {
        patientRepository.deleteAll();
    }

    @Test
    void getPatientsWithoutSearchReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPatientsSearchesByCodeAndName() throws Exception {
        patientService.createPatient(new CreatePatientRequest("PAT-SEARCH1", "Marko", "Ilic"));
        patientService.createPatient(new CreatePatientRequest("PAT-SEARCH2", "Jelena", "Nikolic"));

        mockMvc.perform(get("/api/patients").param("search", "marko"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patientCode").value("PAT-SEARCH1"));

        mockMvc.perform(get("/api/patients").param("search", "search2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patientCode").value("PAT-SEARCH2"));
    }
}
