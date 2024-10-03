package edu.capstone4.userserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // 发送验证码邮件的函数
    public void sendVerificationCode(String to, String subject, String text) {
        try {
            // 创建邮件内容
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            // 记录日志：准备发送邮件
            logger.info("Preparing to send email to: " + to + ", Subject: " + subject);

            // 发送邮件
            mailSender.send(message);

            // 记录日志：发送邮件成功
            logger.info("Verification code email sent successfully to: " + to);

        } catch (Exception e) {
            // 如果发送邮件失败，记录错误日志
            logger.error("Error occurred while sending verification code email: " + e.getMessage());
        }
    }
}
