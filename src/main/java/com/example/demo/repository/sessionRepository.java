package com.example.demo.repository;

import com.example.demo.entité.Session;
import com.example.demo.entité.StatutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface sessionRepository extends JpaRepository<Session, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.statutSession = :statut WHERE s.datedebut <= CURRENT_DATE AND s.datefin > CURRENT_DATE AND s.statutSession != 'VALIDEPARADMIN' ")
    void updateSessionsToEncours(StatutSession statut);

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.statutSession = :statut WHERE s.datefin <= CURRENT_DATE AND s.statutSession != 'VALIDEPARADMIN'")
    void updateSessionsToFini(StatutSession statut);


    @Query("SELECT s FROM Session s WHERE s.module.id = :moduleId")
    List<Session> findByModuleId(@Param("moduleId") Long moduleId);

    @Query("SELECT s FROM Session s JOIN FETCH s.module")
    List<Session> findAllSessionsWithModules();

    @Query("SELECT s FROM Session s JOIN s.membres m WHERE m.id = :userId")
    List<Session> findByMembreId(@Param("userId") Long userId);

    long countByStatutSession(StatutSession statutSession);

    long countByStatutSessionNot(StatutSession statutSession);

    @Query("SELECT MAX(s.cout) FROM Session s")
    long findMaxCost();
}
