package com.example.demo.repository;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Session;
import com.example.demo.entité.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembreRepository extends JpaRepository<Membre,Long> {

    List<Membre> findBySessionId(Long sessionId);
    List<Membre> findByUserId(Long userId);
    Optional<Membre> findByUserAndSession(User user, Session session);
    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);

    Membre findBySessionIdAndUserId(Long sessionId, Long userId);


    @Query("SELECT COUNT(DISTINCT m.user) FROM Membre m")
    long countMembers();

    List<Membre> findBySession_Id(Long sessionId);




}
