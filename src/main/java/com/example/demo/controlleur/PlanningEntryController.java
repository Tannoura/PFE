package com.example.demo.controlleur;

import com.example.demo.entité.PlanningEntry;
import com.example.demo.service.planningEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planningEntry")
public class PlanningEntryController {

        @Autowired
       private planningEntryService PlanningEntryService;

    // Endpoint pour créer une nouvelle entrée de planification
    @PostMapping("/{sessionId}")
    public ResponseEntity<PlanningEntry> createPlanningEntry(@PathVariable Long sessionId,
                                                             @RequestBody PlanningEntry request) {
        PlanningEntry planningEntry = new PlanningEntry();
        planningEntry.setDebut(request.getDebut());
        planningEntry.setFin(request.getFin());

        planningEntry.setJour(request.getJour());

        PlanningEntry createdEntry = PlanningEntryService.addPlanningEntry(sessionId, planningEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
    }

    @GetMapping("/session/{sessionId}")
    public List<PlanningEntry> getPlanningEntriesBySessionId(@PathVariable Long sessionId) {
        return PlanningEntryService.getPlanningEntriesBySessionId(sessionId);
    }

    @GetMapping("/planning-entries")
    public List<PlanningEntry> getPlanningEntriesByDate(@RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return PlanningEntryService.getPlanningEntriesByDate(localDate);
    }

    @GetMapping("/entries")
    public List<PlanningEntry> getPlanningEntries(@RequestParam("sessionId") Long sessionId) {
        return PlanningEntryService.getPlanningEntriesForSession(sessionId);
    }
}
