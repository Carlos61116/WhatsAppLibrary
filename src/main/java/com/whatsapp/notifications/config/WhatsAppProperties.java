package com.whatsapp.notifications.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Main WhatsApp properties class that maps the entire whatsapp section from YAML
 * 
 * Structure in YAML:
 * whatsapp:
 *   enabled: true
 *   phone-number-id: "..."
 *   access-token: "..."
 *   default-language: es
 *   templates:
 *     hello_world:
 *       languages:
 *         es: "template_name"
 *       params: 4
 */
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppProperties {

    private boolean enabled = false;
    private String phoneNumberId;
    private String accessToken;
    private String webhookToken;
    private int maxRetries = 3;
    private String defaultLanguage = "es";
    
    // Templates map
    private Map<String, TemplateConfig> templates = new HashMap<>();

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getWebhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Map<String, TemplateConfig> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, TemplateConfig> templates) {
        this.templates = templates;
    }

    /**
     * Validates that required WhatsApp configuration is present when enabled
     */
    public void validate() throws IllegalArgumentException {
        if (enabled) {
            if (phoneNumberId == null || phoneNumberId.isEmpty()) {
                throw new IllegalArgumentException("whatsapp.phone-number-id is required when enabled=true");
            }
            if (accessToken == null || accessToken.isEmpty()) {
                throw new IllegalArgumentException("whatsapp.access-token is required when enabled=true");
            }
            // webhookToken and defaultLanguage are optional
        }
    }

    /**
     * Template configuration
     */
    public static class TemplateConfig {
        private Map<String, String> languages = new HashMap<>();
        private int params = 0;
        private java.util.List<String> buttons = java.util.List.of();

        public Map<String, String> getLanguages() {
            return languages;
        }

        public void setLanguages(Map<String, String> languages) {
            this.languages = languages;
        }

        public int getParams() {
            return params;
        }

        public void setParams(int params) {
            this.params = params;
        }

        public java.util.List<String> getButtons() {
            return buttons;
        }

        public void setButtons(java.util.List<String> buttons) {
            this.buttons = buttons;
        }
    }
}
