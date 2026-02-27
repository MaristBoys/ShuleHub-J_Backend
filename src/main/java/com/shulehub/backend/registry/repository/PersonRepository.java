package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    // 1. Fondamentale per il collegamento con l'account Utente
    Optional<Person> findById(UUID id);

    // 2. Ricerca per Nome Completo (Sfrutta l'indice GIN full_name_trgm di Postgres)
    // Usiamo una query nativa per sfruttare l'operatore ILIKE o % (trigram search)
    @Query(value = "SELECT * FROM persons WHERE full_name ILIKE CONCAT('%', :searchTerm, '%')", nativeQuery = true)
    List<Person> searchByFullName(@Param("searchTerm") String searchTerm);


    // 3. Verifica esistenza per logica di business (es. prima di inserire un duplicato)
    boolean existsById(UUID id);

    // 4. Ricerca filtrata per ruolo (utile per liste rapide)
    List<Person> findByIsEmployeeTrue();
    List<Person> findByIsStudentTrue();
}
