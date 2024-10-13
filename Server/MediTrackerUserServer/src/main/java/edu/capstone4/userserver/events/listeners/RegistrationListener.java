package edu.capstone4.userserver.events.listeners;

import edu.capstone4.userserver.events.registers.RegistrationCompleteEvent;
import edu.capstone4.userserver.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 获取用户信息和验证码
        String email = event.getEmail();
        String verificationCode = event.getVerificationCode();

        // 调用邮件服务发送验证码
        emailService.sendVerificationCode(email, verificationCode);
    }
}
