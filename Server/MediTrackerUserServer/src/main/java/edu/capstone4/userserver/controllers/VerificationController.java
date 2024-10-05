package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.models.Verification;
import edu.capstone4.userserver.repository.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;

    // Generate verification code and store it in the database
    @PostMapping("/generate/{email}")
    public String generateVerificationCode(@PathVariable String email) {
        // Generate a unique verification code
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);

        // Define expiration time (e.g., 10 minutes from now)
        Instant expiryTime = Instant.now().plus(Duration.ofMinutes(10));

        // Save the verification code in the database
        Verification verification = new Verification(email, verificationCode, Instant.now(), expiryTime);
        verificationRepository.save(verification);

        // Here, integrate sending an email to the user with the verification code
        // emailService.sendSimpleEmail(email, "Your verification code", verificationCode);

        return "Verification code generated and sent to email.";
    }

    // Verify if the verification code is correct
    @PostMapping("/verify")
    public String verifyCode(@RequestParam String email, @RequestParam String code) {
        // Retrieve verification from the database by email
        Verification verification = verificationRepository.findByEmail(email).orElse(null);

        if (verification != null && verification.getVerificationCode().equals(code)) {
            // Check if the verification code is still valid (not expired)
            if (Instant.now().isBefore(verification.getExpiryTime())) {
                return "Verification successful!";
            } else {
                return "Verification code has expired.";
            }
        } else {
            return "Invalid verification code.";
        }
    }
}
