package com.example.demo.service;

import com.example.demo.entité.PlanningEntry;
import com.example.demo.entité.PlanningType;
import com.example.demo.entité.Session;
import com.example.demo.repository.planningEntryRepository;
import com.example.demo.repository.sessionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class planningEntryService {

    @Autowired
    private planningEntryRepository PlanningEntryRepository;
    @Autowired
    private sessionRepository sessionRepository;

    @Transactional
    public PlanningEntry addPlanningEntry(Long sessionId, PlanningEntry planningEntry) {
        // Vérification de l'existence de la session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + sessionId));

        // Associer l'entrée de planification à la session
        planningEntry.setSession(session);

        // Si le planningType de la session est DAY, définir le jour de la semaine
        if (session.getPlanningType() == PlanningType.EVERYWEEK) {
            planningEntry.setJour(planningEntry.getJour());
        }

        if (session.getPlanningType() == PlanningType.MOINS7) {
            planningEntry.setJour(planningEntry.getJour());
        }

        if (session.getPlanningType() == PlanningType.EVERYDAY) {
            planningEntry.setJour(null);
        }

        // Enregistrer l'entrée de planification
        return PlanningEntryRepository.save(planningEntry);
    }

    public List<PlanningEntry> getPlanningEntriesBySessionId(Long sessionId) {
        return PlanningEntryRepository.findBySessionId(sessionId);
    }

    public List<PlanningEntry> getPlanningEntriesByDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return PlanningEntryRepository.findByJour(dayOfWeek);
    }


    public List<PlanningEntry> getPlanningEntriesForSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        LocalDate startDate = session.getDatedebut();
        LocalDate endDate = session.getDatefin();

        List<PlanningEntry> allEntries = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            allEntries.addAll(getPlanningEntriesForDate(date, session));
        }
        return allEntries;
    }

    private List<PlanningEntry> getPlanningEntriesForDate(LocalDate date, Session session) {
        return PlanningEntryRepository.findBySessionAndJour(session, date.getDayOfWeek());
    }



}
