package com.shulehub.backend.student_assignment.repository;

import com.shulehub.backend.student_assignment.model.view.StudentPickerView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentPickerViewRepository extends JpaRepository<StudentPickerView, UUID> {

    /**
     * Ricerca globale con paginazione.
     * Filtra per nome (case-insensitive) o PREM Number.
     */
    @Query("SELECT v FROM StudentPickerView v WHERE " +
           "(:searchTerm IS NULL OR LOWER(v.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
           "(:searchTerm IS NULL OR v.premNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<StudentPickerView> searchStudents(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Esempio di filtro aggiuntivo: cerca solo chi non è ancora stato assegnato nell'anno corrente.
     * Utile per evitare di mostrare studenti già "occupati" nel picker.
     */
    @Query("SELECT v FROM StudentPickerView v WHERE " +
           "(v.lastYearId IS NULL OR v.lastYearId <> :currentYearId) AND " +
           "(LOWER(v.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<StudentPickerView> findAvailableForYear(
        @Param("searchTerm") String searchTerm, 
        @Param("currentYearId") Short currentYearId, 
        Pageable pageable
    );
}