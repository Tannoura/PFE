package com.example.demo.service;

import com.example.demo.entité.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.posteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthenticationService {

    @Autowired
    private posteRepository posterepository;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepo, JwtService jwtService
            , PasswordEncoder passwordEncoder
            ,AuthenticationManager authenticationManager, EmailService emailService) {

        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.emailService=emailService;
    }


    private boolean isValidEmail(String address) {
        // Vérifier si l'email est valide
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (address == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    public AuthenticationResponse registerAdmin(Admin request) {
        Admin user = new Admin();

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

       // user.setRole(request.getRole());

        user.setUsername(request.getUsername());

        user = userRepo.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse registerSalarie(salarié request) {
        if (!isValidEmail(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address");
        }

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Poste newPoste = new Poste();
        newPoste.setSpecialite(request.getPoste().getSpecialite());

        // Save the new Poste
        Poste savedPoste = posterepository.save(newPoste);

        salarié user = new salarié();

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPoste(savedPoste);
        user.setUsername(request.getUsername());
        user.setEmailConfirmed(false);
        user = userRepo.save(user);
        String token = jwtService.generateToken(user);
        String confirmationLink = "http://localhost:9000/confirm?token=" + token;
        emailService.sendEmail(user.getUsername(), "Confirmation Email", "Veuillez confirmer le compte dans cet email " + confirmationLink + "\n votre mot de passe est password par défaut , change le dans l'application");
        return new AuthenticationResponse(token);
    }



    public AuthenticationResponse authenticateAdmin(Admin request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepo.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticateSalarie(salarié request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepo.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }




    public User updateUser(salarié updatedUser) {
        // Vérifier si l'utilisateur existe
        User existingUser = userRepo.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mettre à jour les champs modifiables
        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        // Enregistrer et retourner l'utilisateur mis à jour
        return userRepo.save(existingUser);
    }
    public String initiatePasswordReset(String email) {
        User user = userRepo.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        String resetLink = "http://localhost:4200/forgetPassword?token=" +token ;
        emailService.sendEmail(email,"Réinstallation mot de passe",resetLink);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isValid(token, user)) {
            throw new RuntimeException("Invalid token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
