// RoomRepository.java
package com.shulehub.backend.school_structure.repository;

import com.shulehub.backend.school_structure.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Short> {
}