package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Questa query serve al tuo AuthService per trovare l'impiegato 
    // partendo dall'ID dello User che ha appena fatto login.
    @Query("SELECT e FROM Employee e JOIN e.person p WHERE p.idUser = :userId")
    Optional<Employee> findByUserId(@Param("userId") UUID userId);
}