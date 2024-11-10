package com.example.demo.controlleur;

import com.example.demo.entité.Admin;
import com.example.demo.entité.AuthenticationResponse;
import com.example.demo.entité.User;
import com.example.demo.entité.salarié;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.userService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LoginRegisterController {
    private final AuthenticationService authenticationService;
    private final userService usSer;

    public LoginRegisterController(AuthenticationService authenticationService,userService usSer) {
        this.authenticationService = authenticationService;
        this.usSer=usSer;
    }


    @PostMapping("/registerAdmin")
    public ResponseEntity<AuthenticationResponse> registerAdmin (@RequestBody Admin request){

        return ResponseEntity.ok(authenticationService.registerAdmin(request));
    }

    @PostMapping("/registerSalarie")
    public ResponseEntity<AuthenticationResponse> registerSalarie (@RequestBody salarié request){

        return ResponseEntity.ok(authenticationService.registerSalarie(request));
    }


    @PostMapping("/loginAdmin")
    public ResponseEntity<AuthenticationResponse> loginAdmin (@RequestBody Admin request){
        return ResponseEntity.ok(authenticationService.authenticateAdmin(request));
    }

    @PostMapping("/loginSalarie")
    public ResponseEntity<AuthenticationResponse> loginSalarie (@RequestBody salarié request){
        return ResponseEntity.ok(authenticationService.authenticateSalarie(request));
    }


    @PutMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody salarié updatedUser) {
        updatedUser.setId(userId);
        return authenticationService.updateUser(updatedUser);
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePasswordReset(@RequestParam String email) {
        String token =  authenticationService.initiatePasswordReset(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authenticationService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }


    @GetMapping("/countSalariés")
    public long getCountSalariés() {
        return usSer.getSalariéCount();
    }

    @GetMapping("/allSalarie")
    public List<salarié> getAllSalariés() {
        return usSer.getAllSalariés();
    }
}
