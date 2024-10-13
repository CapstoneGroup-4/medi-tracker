package edu.capstone4.userserver.events.registers;

import edu.capstone4.userserver.models.User;
import org.springframework.context.ApplicationEvent;

public class RegistrationCompleteEvent extends ApplicationEvent {

    private final String email;
    private final String verificationCode;

    public RegistrationCompleteEvent(String email, String verificationCode) {
        super(email);
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
