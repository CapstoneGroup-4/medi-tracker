package edu.capstone4.userserver.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    HtmlTempleService htmlTempleService;

    @Value("${app.mail.senderEmail}")
    private String senderEmail;

    @Value("${app.mail.emailSubject}")
    private String emailSubject;

    @Value("${app.mail.apikey}")
    private String apikey;

    // 发送验证码邮件的函数
    public void sendVerificationCode(String to, String verificationCode) {
        try {
            Resend resend = new Resend(apikey);
            String emailContent = htmlTempleService.loadEmailTemplateWithCode(verificationCode);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(senderEmail)
                    .to(to)
                    .subject(emailSubject)
                    .html(emailContent)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (IOException | ResendException e) {
            throw new RuntimeException(e);
        }
    }
}
