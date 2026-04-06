package com.shulehub.backend.teacher_assignment.repository;

import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Sta utilizzando una tecnica chiamata "Constructor Projection"
//Invece di scaricare l'intera entità TeacherAssignment (che contiene solo ID numerici)
//e poi dover fare altre query per i nomi delle classi o delle materie,
//la query crea direttamente il TeacherAssignmentDTO


@Repository
public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Integer> {

    @Query("SELECT new com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO(" +
       "yr.id, " +
       "r.roomName, " +
       "s.id, " +
       "s.subjectNameKsw, " + 
       "s.subjectNameEng, " + 
       "s.subjectAbbr, " + 
       "s.subjectDescription, " + 
       "ta.classTeacher) " + 
       "FROM TeacherAssignment ta " +
       "JOIN ta.yearRoom yr " +
       "JOIN yr.room r " +
       "JOIN ta.subject s " +
       "WHERE ta.employee.id = :employeeId " +
       "AND yr.year.id = :activeYearId")
    List<TeacherAssignmentDTO> findTeacherContext(
            @Param("employeeId") UUID employeeId, 
            @Param("activeYearId") Short activeYearId
    );

    /**
     * Recupera tutti i docenti e le materie assegnate a una specifica YearRoom.
     * Spring JPA navigherà automaticamente la relazione 'yearRoom' e userà l'ID.
     */
    List<TeacherAssignment> findByYearRoomId(Integer yearRoomId);




    /**
     * Recupera l'assegnazione del Class Teacher per una specifica YearRoom.
     * Per convenzione, il Class Teacher ha subject = null e classTeacher = true.
     */
    Optional<TeacherAssignment> findByYearRoomIdAndSubjectIsNullAndClassTeacherTrue(Integer yearRoomId);

    /**
     * Recupera tutte le assegnazioni (Staffing) per una stanza, escluso il Class Teacher.
     * Utile per popolare il tab Staffing nel modale.
     */
    @Query("SELECT ta FROM TeacherAssignment ta " +
        "LEFT JOIN FETCH ta.subject s " +   // LEFT JOIN per vedere la materia anche senza docente
        "LEFT JOIN FETCH ta.employee e " +  // LEFT JOIN per vedere il record anche se id_employee è NULL
        "LEFT JOIN FETCH e.person p " +
        "WHERE ta.yearRoom.id = :yearRoomId " +
        "AND ta.subject IS NOT NULL " +     // Escludiamo il Class Teacher
        "ORDER BY s.subjectNameEng ASC")
    List<TeacherAssignment> findStaffingByYearRoomId(@Param("yearRoomId") Integer yearRoomId);




    /**
     * Trova un'assegnazione specifica per materia.
     * Utile quando dobbiamo aggiornare il docente di una materia già presente.
     */
    Optional<TeacherAssignment> findByYearRoomIdAndSubjectId(Integer yearRoomId, Short subjectId);

    /**
     * Verifica se un docente è già impegnato come Class Teacher in un'altra stanza
     * nello stesso anno scolastico (evita sovrapposizioni).
     */
    boolean existsByEmployeeIdAndYearRoomYearIdAndClassTeacherTrue(UUID employeeId, Short yearId);


    // Metodi per verificare l'esistenza di voti (marks) associati a una materia o a una stanza, 
    // utili per la logica di disattivazione o cancellazione della configurazione cfg_yearroom_subject_teacher.

    /**
     * Verifica se esistono voti accademici nella tabella 'marks'.
     * Utilizziamo una query SQL nativa (nativeQuery = true) perchè non stiamo mappando una entità specifica,
     * ma solo verificando l'esistenza di record.
     */
    @Query(value = "SELECT EXISTS (SELECT 1 FROM marks WHERE id_subject = :subjectId AND id_year = :yearId)", 
        nativeQuery = true)
    boolean hasPhysicalMarks(@Param("subjectId") Short subjectId, @Param("yearId") Short yearId);

    /**
     * Verifica se esistono voti di condotta nella tabella 'conduct_marks'.
     * Dobbiamo collegare i voti agli studenti della specifica stanza (id_yearroom).
     * Nello schema: conduct_marks.id_student -> students.id
     */
    @Query(value = "SELECT EXISTS (" +
                "  SELECT 1 FROM conduct_marks cm " +
                "  JOIN students s ON cm.id_student = s.id " +
                "  WHERE s.id_yearroom = :yearRoomId AND cm.id_year = :yearId" +
                ")", 
        nativeQuery = true)
    boolean hasConductMarks(@Param("yearRoomId") Integer yearRoomId, @Param("yearId") Short yearId);

    /**
     * Recupera gli ID delle materie già assegnate a una YearRoom.
     * Utile per disabilitare le opzioni già assegnate nel dropdown del frontend.
     */
    @Query("SELECT ta.subject.id FROM TeacherAssignment ta " +
        "WHERE ta.yearRoom.id = :yearRoomId " +
        "AND ta.subject IS NOT NULL")
    List<Short> findAssignedSubjectIds(@Param("yearRoomId") Integer yearRoomId);


}


