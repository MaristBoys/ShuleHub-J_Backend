package com.shulehub.backend.student_assignment.service;

import com.shulehub.backend.student_assignment.model.view.StudentPickerView;
import com.shulehub.backend.student_assignment.repository.StudentPickerViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentAssignmentService {

    private final StudentPickerViewRepository studentPickerViewRepository;

    /**
     * Recupera la lista paginata degli studenti attivi per il picker.
     */
    @Transactional(readOnly = true)
    public Page<StudentPickerView> searchStudentsForPicker(String searchTerm, int page, int size) {
        // Ordiniamo per nome di default per il picker
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return studentPickerViewRepository.searchStudents(searchTerm, pageable);
    }
    
    // Qui aggiungeremo in seguito: assignStudent, moveStudent, bulkCopy...
}