package com.shulehub.backend.auth.repository; // <--- Indispensabile per il modulo Auth

import com.shulehub.backend.auth.model.entity.RefPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//import java.util.List;
import java.util.Set;



@Repository
public interface PermissionRepository extends JpaRepository<RefPermission, Short> {

    @Query("SELECT DISTINCT rel.permission.permissionCode FROM RelProfilePermission rel " +
           "WHERE rel.idProfile = :profileId " +
           "AND rel.permission.permissionIsActive = true " + 
           "AND rel.profilePermissionIsActive = true")
    Set<String> findCodesByProfileId(@Param("profileId") Short profileId);
}
