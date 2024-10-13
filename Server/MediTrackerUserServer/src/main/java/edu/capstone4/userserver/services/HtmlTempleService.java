package edu.capstone4.userserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class HtmlTempleService {
    @Autowired
    private ResourceLoader resourceLoader;

    public String loadEmailTemplateWithCode(String code) throws IOException {
        var resource = resourceLoader.getResource("classpath:templates/verificationEmailTemplate.html");
        String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return content.replace("${code}", code); // 替换验证码占位符
    }
}
