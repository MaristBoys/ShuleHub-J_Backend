package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Questa query serve al tuo AuthService per trovare l'impiegato 
    // partendo dall'ID dello User che ha appena fatto login.
    @Query("SELECT e FROM Employee e WHERE e.id = :personId")
    Optional<Employee> findByPersonId(@Param("personId") UUID personId);


    // Conteggio per la card dashboard
    long countByEmployeeIsActiveTrue();
    // Conta chi è stato assunto tra il 01-01 e il 31-12 dell'anno scelto
    long countByHireDateBetween(LocalDate start, LocalDate end);
    // Conta chi ha terminato il rapporto tra il 01-01 e il 31-12 dell'anno scelto
    long countByEmploymentEndDateBetween(LocalDate start, LocalDate end);

}

