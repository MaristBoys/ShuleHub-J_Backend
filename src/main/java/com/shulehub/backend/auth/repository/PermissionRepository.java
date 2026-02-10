@Repository
public interface PermissionRepository extends JpaRepository<RefPermission, Short> {

    @Query("SELECT p.permissionName FROM RefPermission p " +
           "JOIN RelProfilePermission rel ON p.id = rel.idPermission " +
           "WHERE rel.idProfile = :profileId")
    Set<String> findNamesByProfileId(@Param("profileId") Short profileId);
}
