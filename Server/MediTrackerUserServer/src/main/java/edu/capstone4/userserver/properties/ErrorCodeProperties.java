package edu.capstone4.userserver.properties;

import edu.capstone4.userserver.config.ErrorCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.error-codes")
public class ErrorCodeProperties {
    private Map<String, ErrorCodeConfig> codes;

    public Map<String, ErrorCodeConfig> getCodes() {
        return codes;
    }

    public void setCodes(Map<String, ErrorCodeConfig> codes) {
        this.codes = codes;
    }

    public ErrorCodeConfig getCode(String key) {
        return codes.get(key);
    }

}
