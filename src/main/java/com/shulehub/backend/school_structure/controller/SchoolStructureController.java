package com.shulehub.backend.school_structure.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_structure.model.entity.Room;
import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.service.SchoolStructureService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/school-structure")
@RequiredArgsConstructor
public class SchoolStructureController {
    private final SchoolStructureService schoolStructureService;

    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Year>>> getAllYears() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Years retrieved", schoolStructureService.getAllYears()));
    }

    @PostMapping("/years")
    public ResponseEntity<ApiResponse<Year>> createYear() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Year created", schoolStructureService.createNextYear()));
    }

    @PatchMapping("/years/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateYear(@PathVariable Short id) {
        schoolStructureService.updateActiveYear(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Year activated", null));
    }


    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<Room>> getRoomById(@PathVariable Short id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Room retrieved", schoolStructureService.getRoomById(id)));
    }



}