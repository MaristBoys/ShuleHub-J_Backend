package com.shulehub.backend.auth.repository; // <--- Indispensabile per il modulo Auth

import com.shulehub.backend.auth.model.entity.RefPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface PermissionRepository extends JpaRepository<RefPermission, Short> {

    @Query("SELECT p.permissionName FROM RefPermission p " +
           "JOIN RelProfilePermission rel ON p.id = rel.idPermission " +
           "WHERE rel.idProfile = :profileId")
    Set<String> findNamesByProfileId(@Param("profileId") Short profileId);
}
