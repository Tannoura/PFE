package com.example.demo.controlleur;

import com.example.demo.entité.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.JwtService;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EmailConfirmationController {
    private final JwtService jwtService;
    private final UserRepository userRepo;

    private final EmailService emailService;
    private final JavaMailSender javaMailSender;


    public EmailConfirmationController(JwtService jwtService, UserRepository userRepo,EmailService emailService,JavaMailSender javaMailSender) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.emailService=emailService;
        this.javaMailSender=javaMailSender;
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtService.isValid(token, user)) {
            user.setEmailConfirmed(true);
            userRepo.save(user);
            return ResponseEntity.ok("Email confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/send")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        emailService.sendEmail(to, subject, text);
        return "Email sent successfully";
    }


    @PostMapping("/sendWithAttach")
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam("attachment") MultipartFile attachment) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment(attachment.getOriginalFilename(), () -> {
                try {
                    return attachment.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            javaMailSender.send(message);

            return ResponseEntity.ok("Email sent successfully!");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }
}
