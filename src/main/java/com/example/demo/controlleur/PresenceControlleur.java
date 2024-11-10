package com.example.demo.controlleur;

import com.example.demo.entité.Presence;
import com.example.demo.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presence")
public class PresenceControlleur {

    @Autowired
    private PresenceService presenceService;

    @GetMapping("/session/{sessionId}/presences")
    public List<Presence> getPresencesBySession(@PathVariable Long sessionId) {
        return presenceService.getPresencesBySessionId(sessionId);
    }

    @PostMapping("/membre/{membreId}/presence")
    public void markPresence(@PathVariable Long membreId, @RequestParam boolean present ,@RequestParam LocalDate date) {
        presenceService.markPresence(membreId, present,date);
    }


    @GetMapping("/exists")
    public boolean checkPresenceExists(@RequestParam Long membreId,
                                       @RequestParam LocalDate jour) {
        return presenceService.presenceExists(membreId, jour);
    }

    @PutMapping("/update")
    public void updatePresence(@RequestParam Long membreId, @RequestParam boolean present,@RequestParam LocalDate date) {
        presenceService.updatePresence(membreId, present,date);
    }

    @GetMapping
    public List<Presence> getPresences(
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("userId") Long userId) {
        return presenceService.getPresencesBySessionAndUser(sessionId, userId);
    }

    @GetMapping("/user/{userId}/session/{sessionId}/taux")
    public double getTauxDePresenceParSessionAndUser(
            @PathVariable Long sessionId,
            @PathVariable Long userId) {
        return presenceService.calculerTauxDePresenceParSessionAndUser(sessionId, userId);
    }


}

