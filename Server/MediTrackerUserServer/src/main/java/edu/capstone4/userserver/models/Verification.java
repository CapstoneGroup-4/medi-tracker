package edu.capstone4.userserver.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "verification")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private Instant codeGeneratedTime;

    @Column(nullable = false)
    private Instant expiryTime;

    public Verification() {
    }

    public Verification(String email, String verificationCode, Instant codeGeneratedTime, Instant expiryTime) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.codeGeneratedTime = codeGeneratedTime;
        this.expiryTime = expiryTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Instant getCodeGeneratedTime() {
        return codeGeneratedTime;
    }

    public void setCodeGeneratedTime(Instant codeGeneratedTime) {
        this.codeGeneratedTime = codeGeneratedTime;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }
}
