package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.view.EmployeeProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeProfileViewRepository extends JpaRepository<EmployeeProfileView, java.util.UUID> {
    
    Optional<EmployeeProfileView> findByLoginEmailIgnoreCase(String loginEmail);
}
