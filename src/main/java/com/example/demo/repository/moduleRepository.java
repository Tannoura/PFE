package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entité.Module;

import java.util.List;
import java.util.Optional;

@Repository
public interface moduleRepository extends JpaRepository<Module,Long> {
    List<Module> findByMatiere(String matiere);

    @Query("SELECT m FROM Module m JOIN FETCH m.organismes WHERE m.id = :moduleId")
    Optional<Module> findByIdWithOrganismes(@Param("moduleId") Long moduleId);

    boolean existsByMatiere(String matiere);

    @Query("SELECT COUNT(m) FROM Module m WHERE m.organismes IS EMPTY")
    long countModulesSansOrganismes();


}
