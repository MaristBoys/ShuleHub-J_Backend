// RoomRepository.java
package com.shulehub.backend.school_structure.repository;

import com.shulehub.backend.school_structure.model.entity.Room;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


// metodo per recuperare il numero della room (es romm 11  --> 11)
    // utilizzato per la ghost cell

    @Repository
    public interface RoomRepository extends JpaRepository<Room, Short> {
        
        @Query("SELECT r FROM Room r JOIN FETCH r.form WHERE r.roomNum = :roomNum")
        Optional<Room> findByRoomNum(@Param("roomNum") Short roomNum);
    }