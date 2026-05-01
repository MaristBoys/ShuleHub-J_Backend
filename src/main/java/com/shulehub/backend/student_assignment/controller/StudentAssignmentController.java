package com.shulehub.backend.student_assignment.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.student_assignment.model.view.StudentPickerView;
import com.shulehub.backend.student_assignment.service.StudentAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student-assignment")
@RequiredArgsConstructor
public class StudentAssignmentController {

    private final StudentAssignmentService studentAssignmentService;

    /**
     * Endpoint per la ricerca paginata degli studenti nel picker.
     * * @param query Stringa di ricerca (Nome o PREM Number)
     * @param page  Indice della pagina (default 0)
     * @param size  Numero di record per pagina (default 50)
     * @return ResponseEntity contenente l'ApiResponse con la pagina di risultati
     */
    @GetMapping("/picker-search")
    public ResponseEntity<ApiResponse<Page<StudentPickerView>>> searchStudents(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Page<StudentPickerView> results = studentAssignmentService.searchStudentsForPicker(query, page, size);
        
        // Creazione dell'istanza ApiResponse usando il costruttore @AllArgsConstructor
        ApiResponse<Page<StudentPickerView>> response = new ApiResponse<>(
            true, 
            "Students retrieved successfully", 
            results
        );
        
        return ResponseEntity.ok(response);
    }
}