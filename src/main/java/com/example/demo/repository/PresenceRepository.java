package com.example.demo.repository;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findByMembre_Session_Id(Long sessionId);

    List<Presence> findByMembre_Session_IdAndMembre_User_Id(Long sessionId, Long userId);


    boolean existsByMembreId(Long membreId);

    List<Presence> findByMembreId(Long membreId);

    boolean existsByMembreIdAndJour(Long membreId, LocalDate date);
    Presence findByMembreIdAndJour(Long membreId, LocalDate date);

    List<Presence> findByMembreIn(List<Membre> membres);



}


