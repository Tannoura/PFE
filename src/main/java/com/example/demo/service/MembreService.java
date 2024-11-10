package com.example.demo.service;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Session;
import com.example.demo.entité.User;
import com.example.demo.repository.MembreRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.sessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembreService {
    @Autowired
    private MembreRepository membreRepository;
    @Autowired

    private UserRepository userRepository;
    @Autowired

    private sessionRepository SessionRepository;


    public void addMembreToSession(Long userId, Long sessionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Session session = SessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        Membre membre = new Membre();
        membre.setUser(user);
        membre.setSession(session);
        membreRepository.save(membre);
    }

    public void deleteMembre(long idO) {
        membreRepository.deleteById(idO);
    }

    public void deleteMembreByIds(long userId,long sessionId) {

        // Récupérer l'utilisateur par userId
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }

        // Récupérer la session par sessionId
        Optional<Session> sessionOpt = SessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new EntityNotFoundException("Session not found with id " + sessionId);
        }

        User user = userOpt.get();
        Session session = sessionOpt.get();

        // Récupérer le membre correspondant à l'utilisateur et la session
        Optional<Membre> membreOpt = membreRepository.findByUserAndSession(user, session);
        if (membreOpt.isEmpty()) {
            throw new EntityNotFoundException("Membre not found for user id " + userId + " and session id " + sessionId);
        }

        Membre membre = membreOpt.get();

        // Supprimer le membre
        membreRepository.delete(membre);
    }

    public List<User> getUsersBySessionId(Long sessionId) {
        // Récupérer les membres de la session
        List<Membre> membres = membreRepository.findBySessionId(sessionId);

        // Extraire les utilisateurs des membres
        List<User> users = membres.stream()
                .map(Membre::getUser)
                .collect(Collectors.toList());
        return users;
    }
    @Transactional(readOnly = true)
    public boolean isMember(Long userId, Long sessionId) {
        return membreRepository.existsByUserIdAndSessionId(userId, sessionId);
    }

    public List<Session> getSessionsByUserId(Long userId) {
        // Récupérer tous les membres associés à cet utilisateur
        List<Membre> membres = membreRepository.findByUserId(userId);

        // Extraire les sessions des membres
        return membres.stream()
                .map(Membre::getSession)
                .collect(Collectors.toList());
    }

    public long countMembers() {
        return membreRepository.countMembers();
    }
    public Membre getMembreBySessionAndUser(Long sessionId, Long userId) {
        return membreRepository.findBySessionIdAndUserId(sessionId, userId);
    }
    }


