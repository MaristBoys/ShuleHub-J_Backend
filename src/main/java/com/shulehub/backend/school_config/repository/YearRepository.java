@Repository
public interface YearRepository extends JpaRepository<Year, Short> {
    // Il metodo magico per il login
    Optional<Year> findByYearIsActiveTrue();
}
