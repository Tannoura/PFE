package com.example.demo.service;

import com.example.demo.entité.*;
import com.example.demo.entité.Module;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.moduleRepository;
import com.example.demo.repository.sessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class sessionService {
    @Autowired
    private sessionRepository sessionepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private moduleRepository moduleRepo;

    public Session saveSession(Session session) {
        return sessionepository.save(session);
    }

    public List<Session> getAllSessions() {
        return sessionepository.findAll();
    }

    public Session getSessionById(Long idSession){
        Optional<Session> session = sessionepository.findById(idSession);
        return session.orElse(null);
    }
    public boolean updateSessionStatus(Long sessionId, StatutSession status) {
        Optional<Session> sessionOptional = sessionepository.findById(sessionId);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            session.setStatutSession(status);
            sessionepository.save(session);
            return true;
        }
        return false;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void updateSessionStatuses() {
        sessionepository.updateSessionsToEncours(StatutSession.ENCOURS);
        sessionepository.updateSessionsToFini(StatutSession.FINI);
    }

    public long getTotalCost() {
        List<Session> sessions = sessionepository.findAll();
        return sessions.stream().mapToLong(Session::getCout).sum();
    }


    public List<Session> getSessionsByModule(Long moduleId) {
        return sessionepository.findByModuleId(moduleId);
    }


    // Méthode pour calculer le taux de présence d'une session
    public double calculatePresenceRate(Session session, List<Presence> presences) {
        // Récupérer les dates de la session
        List<LocalDate> sessionDates = session.getExactPlanningDates(session.getPlanningEntries());
        int totalDays = sessionDates.size();

        if (totalDays == 0) {
            return 0;
        }

        // Compter les jours de présence
        long presentDays = presences.stream()
                .filter(p -> p.isPresent() && sessionDates.contains(p.getJour()))
                .count();

        return (double) presentDays / totalDays * 100;
    }
    public List<Long> getAllSessionIds() {
        // Fetch all sessions
        List<Session> sessions = sessionepository.findAll();

        // Extract and return session IDs
        return sessions.stream()
                .map(Session::getId)
                .collect(Collectors.toList());
    }

    public Map<Long, String> getSessionMatiereMapping() {
        List<Session> sessions = sessionepository.findAllSessionsWithModules();

        // Create a map from session ID to matiere
        return sessions.stream()
                .collect(Collectors.toMap(
                        Session::getId,
                        session -> session.getModule() != null ? session.getModule().getMatiere() : "Unknown"
                ));
    }

    // Get number of sessions validated by admin
    public long getNumberOfSessionsValidatedByAdmin() {
        return sessionepository.countByStatutSession(StatutSession.VALIDEPARADMIN);
    }

    // Get number of sessions not validated by admin
    public long getNumberOfSessionsNotValidatedByAdmin() {
        return sessionepository.countByStatutSessionNot(StatutSession.VALIDEPARADMIN);
    }

    // Get the maximum cost of a session
    public long getMaxSessionCost() {
        return sessionepository.findMaxCost();
    }

    // Calculer le nombre total d'heures pour tous les PlanningEntry de toutes les sessions
    public long calculateTotalPlanningHours() {
        List<Session> sessions = sessionepository.findAll();
        long totalHours = 0;

        for (Session session : sessions) {
            // Obtenir les dates exactes pour cette session
            List<LocalDate> dates = session.getExactPlanningDates(session.getPlanningEntries());

            // Calculer le nombre d'heures pour chaque PlanningEntry
            for (PlanningEntry entry : session.getPlanningEntries()) {
                long hours = ChronoUnit.HOURS.between(entry.getDebut(), entry.getFin());
                totalHours += hours * dates.size();
            }
        }

        return totalHours;
    }


    // Trouver la session la plus proche par rapport à une date donnée
    public Session getClosestSession(LocalDate currentDate) {
        List<Session> sessions = sessionepository.findAll();

        if (sessions.isEmpty()) {
            return null; // Pas de sessions disponibles
        }

        Session closestSession = null;
        long smallestDifference = Long.MAX_VALUE;

        for (Session session : sessions) {
            // Filtrer les sessions dont la date de début est après la date actuelle et dont le statut est INSCRIPTION
            if (session.getDatedebut().isAfter(currentDate) && session.getStatutSession() == StatutSession.INSCRIPTION) {
                // Calculer la différence en jours entre la date actuelle et la date de début de la session
                long startDifference = ChronoUnit.DAYS.between(currentDate, session.getDatedebut());

                // Trouver la session avec la différence la plus petite
                if (startDifference < smallestDifference) {
                    smallestDifference = startDifference;
                    closestSession = session;
                }
            }
        }
        return closestSession;
    }

    // Trouver la session la plus proche à venir pour un salarié donné
    public Session getClosestUpcomingSessionForEmployee(long employeeId, LocalDate currentDate) {
        // Récupérer le salarié
        salarié salarié = (salarié) userRepository.findById(employeeId).orElse(null);

        if (salarié == null) {
            return null; // Salarié non trouvé
        }

        Poste poste = salarié.getPoste();
        if (poste == null) {
            return null; // Poste non trouvé pour le salarié
        }

        // Trouver les modules qui correspondent à la spécialité du poste
        List<Module> modules = moduleRepo.findByMatiere(poste.getSpecialite());

        if (modules.isEmpty()) {
            return null; // Aucun module correspondant à la spécialité du poste
        }

        // Trouver toutes les sessions avec les modules spécifiés et filtrer par statut
        List<Session> sessions = sessionepository.findAll()
                .stream()
                .filter(session -> modules.contains(session.getModule()))
                .filter(session -> session.getStatutSession() == StatutSession.INSCRIPTION)
                .filter(session -> session.getDatedebut().isAfter(currentDate))
                .collect(Collectors.toList());

        if (sessions.isEmpty()) {
            return null; // Pas de sessions correspondant aux critères
        }

        // Trouver la session la plus proche
        Session closestSession = null;
        long smallestDifference = Long.MAX_VALUE;

        for (Session session : sessions) {
            // Calculer la différence en jours entre la date actuelle et la date de début de la session
            long startDifference = ChronoUnit.DAYS.between(currentDate, session.getDatedebut());

            // Trouver la session avec la différence la plus petite
            if (startDifference < smallestDifference) {
                smallestDifference = startDifference;
                closestSession = session;
            }
        }
        return closestSession;
    }
}
