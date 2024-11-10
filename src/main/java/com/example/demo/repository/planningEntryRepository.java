package com.example.demo.repository;

import com.example.demo.entité.PlanningEntry;
import com.example.demo.entité.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface planningEntryRepository extends JpaRepository<PlanningEntry, Long> {

    @Query("SELECT p FROM PlanningEntry p WHERE p.session.id = :sessionId")
    List<PlanningEntry> findBySessionId(@Param("sessionId") Long sessionId);


    List<PlanningEntry> findByJour(DayOfWeek jour);

    List<PlanningEntry> findBySessionAndJour(Session session, DayOfWeek jour);


}
