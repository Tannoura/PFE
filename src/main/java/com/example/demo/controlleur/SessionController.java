package com.example.demo.controlleur;

import com.example.demo.entité.PlanningEntry;
import com.example.demo.entité.Presence;
import com.example.demo.entité.Session;
import com.example.demo.entité.StatutSession;
import com.example.demo.service.PresenceService;
import com.example.demo.service.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")

public class SessionController {

    @Autowired
    private sessionService sessionService;

    @Autowired
    private PresenceService presenceService;

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionService.saveSession(session);
    }

    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/total-cost")
    public long getTotalCost() {
        return sessionService.getTotalCost();
    }
    @PutMapping("/updateSessionstatus/{sessionId}/{status}")
    public ResponseEntity<?> updateCommandeStatus(@PathVariable("sessionId") Long sessionId,
                                                  @PathVariable("status") StatutSession status) {
        boolean updated = sessionService.updateSessionStatus(sessionId, status);
        if (updated) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updated, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/module/{moduleId}")
    public List<Session> getSessionsByModule(@PathVariable Long moduleId) {
        return sessionService.getSessionsByModule(moduleId);
    }

    @GetMapping("/{sessionId}/planning-dates")
    public List<String> getPlanningDates(@PathVariable Long sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found");
        }

        List<PlanningEntry> planningEntries = session.getPlanningEntries(); // Obtenir toutes les entrées de planning
        return session.getExactPlanningDates(planningEntries)
                .stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());
    }

    @GetMapping("/presence-rate")
    public Map<String, Double> getPresenceRates(@RequestParam List<Long> sessionIds) {
        Map<String, Double> rates = new HashMap<>();
        for (Long sessionId : sessionIds) {
            Session session = sessionService.getSessionById(sessionId);
            List<Presence> presences = presenceService.getPresencesBySessionId(sessionId);
            double rate = sessionService.calculatePresenceRate(session, presences);
            rates.put(session.getId() + "", rate); // Utilisez un format approprié pour les clés
        }
        return rates;
    }

    // Endpoint to fetch session ID to matiere mapping
    @GetMapping("/matiere-mapping")
    public Map<Long, String> getSessionMatiereMapping() {
        return sessionService.getSessionMatiereMapping();
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getSessionIds() {
        List<Long> sessionIds = sessionService.getAllSessionIds();
        return ResponseEntity.ok(sessionIds);
    }

    @GetMapping("/sessions-valides")
    public ResponseEntity<Long> getNumberOfSessionsValidatedByAdmin() {
        long count = sessionService.getNumberOfSessionsValidatedByAdmin();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sessions-non-valides")
    public ResponseEntity<Long> getNumberOfSessionsNotValidatedByAdmin() {
        long count = sessionService.getNumberOfSessionsNotValidatedByAdmin();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/cout-maximal")
    public ResponseEntity<Long> getMaxSessionCost() {
        long maxCost = sessionService.getMaxSessionCost();
        return ResponseEntity.ok(maxCost);
    }

    @GetMapping("/total-planning-hours")
    public ResponseEntity<Long> getTotalPlanningHours() {
        long totalHours = sessionService.calculateTotalPlanningHours();
        return ResponseEntity.ok(totalHours);
    }

    @GetMapping("/closest")
    public ResponseEntity<Session> getClosestSession(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Session closestSession = sessionService.getClosestSession(date);
        if (closestSession != null) {
            return ResponseEntity.ok(closestSession);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/closest/{employeeId}")
    public Session getClosestUpcomingSessionForEmployee(
            @PathVariable long employeeId,
            @RequestParam(name = "currentDate", defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate currentDate) {
        return sessionService.getClosestUpcomingSessionForEmployee(employeeId, currentDate);
    }
}
