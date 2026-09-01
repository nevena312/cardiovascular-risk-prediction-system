package com.cardiovascular.repository;

import com.cardiovascular.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<Assessment> findFirstByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Assessment> findTop10ByOrderByCreatedAtDesc();

    List<Assessment> findAllByOrderByCreatedAtDesc();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select a from Assessment a
            where a.createdAt = (
                select max(a2.createdAt) from Assessment a2
                where a2.patient.id = a.patient.id
            )
            """)
    List<Assessment> findLatestAssessmentForEachPatient();

    @Query("""
            select a from Assessment a
            left join fetch a.contributions
            where a.id = :id
            """)
    Optional<Assessment> findDetailsById(@Param("id") Long id);
}
