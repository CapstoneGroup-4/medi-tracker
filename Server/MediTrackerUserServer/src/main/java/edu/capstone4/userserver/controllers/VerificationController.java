package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.config.WebSecurityConfig;
import edu.capstone4.userserver.payload.response.MessageResponse;
import edu.capstone4.userserver.services.EmailService;
import edu.capstone4.userserver.services.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    @Autowired
    EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        log.debug("email:" + decodedEmail);
        String verificationCode = verificationCodeService.generateCode();
        verificationCodeService.saveCode(decodedEmail, verificationCode);
        emailService.sendVerificationCode(decodedEmail, verificationCode);
        return ResponseEntity.ok(new MessageResponse("Verification code sent successfully"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?>  verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = verificationCodeService.verifyCode(email, code);

        if (isValid) {
            verificationCodeService.removeCode(email);
            return ResponseEntity.ok(new MessageResponse("verificationSuccess"));
        }

        return ResponseEntity.ok(new MessageResponse("verificationFailed"));
    }
}

