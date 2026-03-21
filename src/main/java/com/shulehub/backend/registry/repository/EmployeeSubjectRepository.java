package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.EmployeeSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeSubjectRepository extends JpaRepository<EmployeeSubject, Short> {
    
    // Trova tutte le competenze di un determinato docente
    List<EmployeeSubject> findByEmployeeId(UUID employeeId);
    
    // Verifica se un docente è abilitato per una materia specifica
    boolean existsByEmployeeIdAndSubjectId(UUID employeeId, Short subjectId);
}
