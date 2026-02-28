package com.shulehub.backend.registry.service;

import com.shulehub.backend.registry.model.dto.StudentSummaryDTO;
import com.shulehub.backend.registry.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public StudentSummaryDTO getStudentSummary(Short year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        return new StudentSummaryDTO(
            studentRepository.countByStudentIsActiveTrue(),
            studentRepository.countByEnrollmentDateBetween(start, end),
            studentRepository.countByEndDateBetween(start, end)
        );
    }
}