package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.view.EmployeeProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeProfileViewRepository extends JpaRepository<EmployeeProfileView, String> {
    
    // Trova il profilo tramite l'email di Google
    Optional<EmployeeProfileView> findByEmailIgnoreCase(String workEmail);
}
