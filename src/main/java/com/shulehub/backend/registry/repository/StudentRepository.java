package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    // Conteggio totali attivi
    long countByStudentIsActiveTrue();

    // Studenti nuovi dell'anno (Enrolled)
    long countByEnrollmentDateBetween(LocalDate start, LocalDate end);

    // Studenti che hanno lasciato nell'anno (End Date o Dropped Date)
    // Usiamo l'endDate per coerenza con la logica amministrativa
    long countByEndDateBetween(LocalDate start, LocalDate end);
}