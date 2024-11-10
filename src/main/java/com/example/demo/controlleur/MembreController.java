package com.example.demo.controlleur;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Session;
import com.example.demo.entité.User;
import com.example.demo.service.MembreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membres")
public class MembreController {
    @Autowired
    private MembreService membreService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addMembre(@RequestParam Long userId, @RequestParam Long sessionId) {
        membreService.addMembreToSession(userId, sessionId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Membre ajouté avec succès");
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/delete/{membreId}")
    public ResponseEntity<Void> deleteMembre(@PathVariable long membreId) {
        membreService.deleteMembre(membreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMembreByIds(@RequestParam Long userId, @RequestParam Long sessionId) {
        membreService.deleteMembreByIds(userId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/session/{sessionId}")
    public List<User> getAllMembres(@PathVariable Long sessionId) {
        return membreService.getUsersBySessionId(sessionId);
    }

    @GetMapping("/isMember")
    public boolean checkMembershipStatus(@RequestParam Long userId, @RequestParam Long sessionId) {
        return membreService.isMember(userId, sessionId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getSessionsByUserId(@PathVariable Long userId) {
        List<Session> sessions = membreService.getSessionsByUserId(userId);
        return ResponseEntity.ok(sessions);
    }



    @GetMapping("/count")
    public long countMembers() {
        return membreService.countMembers();
    }


    @GetMapping("/session/{sessionId}/user/{userId}")
    public Membre getMembreBySessionAndUser(@PathVariable Long sessionId, @PathVariable Long userId) {
        return membreService.getMembreBySessionAndUser(sessionId, userId);
    }
}