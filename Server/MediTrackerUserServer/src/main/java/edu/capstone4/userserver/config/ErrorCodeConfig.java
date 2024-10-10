package edu.capstone4.userserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.error-codes")
public class ErrorCodeConfig {
    private Map<String, ErrorCode> codes;

    public Map<String, ErrorCode> getCodes() {
        return codes;
    }

    public void setCodes(Map<String, ErrorCode> codes) {
        this.codes = codes;
    }

    public ErrorCode getCode(String key) {
        return codes.get(key);
    }

}
