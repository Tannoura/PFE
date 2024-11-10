package com.example.demo.service;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Presence;
import com.example.demo.entité.Session;
import com.example.demo.entité.User;
import com.example.demo.repository.MembreRepository;
import com.example.demo.repository.PresenceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.sessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PresenceService {
    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private sessionRepository sessionRepo;

    public List<Presence> getPresencesBySessionId(Long sessionId) {
        return presenceRepository.findByMembre_Session_Id(sessionId);
    }

    public void markPresence(Long membreId, boolean present, LocalDate date) {
        Membre membre = membreRepository.findById(membreId).orElseThrow(() -> new RuntimeException("Membre not found"));
        Presence presence = new Presence();
        presence.setMembre(membre);
        presence.setPresent(present);
        presence.setJour(date);

        presenceRepository.save(presence);
    }
    public boolean presenceExists(Long membreId) {
        return presenceRepository.existsByMembreId(membreId);
    }

    public boolean presenceExists(Long membreId, LocalDate jour) {
        return presenceRepository.existsByMembreIdAndJour(membreId, jour);
    }
    public void updatePresence(Long membreId, boolean present, LocalDate jour) {
        Membre membre = membreRepository.findById(membreId).orElseThrow(() -> new RuntimeException("Membre not found"));
        Presence presence = presenceRepository.findByMembreIdAndJour(membreId,jour);
        if (presence != null) {
            presence.setPresent(present);
            presenceRepository.save(presence);
        } else {
            Presence newPresence = new Presence();
            newPresence.setMembre(membre);
            newPresence.setPresent(present);
            newPresence.setJour(jour);
            presenceRepository.save(newPresence);
        }
    }

    public List<Presence> getPresencesBySessionAndUser(Long sessionId, Long userId) {
        return presenceRepository.findByMembre_Session_IdAndMembre_User_Id(sessionId, userId);
    }



    public double calculerTauxDePresenceParSessionAndUser(Long sessionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));

        // Obtenez la liste des membres de cette session
        List<Membre> membres = membreRepository.findByUserId(userId);
        if (membres.isEmpty() || membres.stream().noneMatch(m -> m.getSession().equals(session))) {
            throw new RuntimeException("L'utilisateur n'est pas membre de cette session.");
        }

        // Obtenez les présences pour cette session
        List<Presence> presences = presenceRepository.findByMembre_Session_IdAndMembre_User_Id(sessionId, userId);
        List<LocalDate> sessionDates = session.getExactPlanningDates(session.getPlanningEntries());

        // Calcul du taux de présence
        int totalDays = sessionDates.size();
        if (totalDays == 0) {
            return 0;  // Pas de jours de session
        }

        long presentDays = presences.stream()
                .filter(p -> p.isPresent() && sessionDates.contains(p.getJour()))
                .count();

        return (double) presentDays / totalDays * 100;
    }



}
