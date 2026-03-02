package com.whatsapp.notifications.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.whatsapp.notifications.client.WhatsAppClient;
import com.whatsapp.notifications.services.WhatsAppNotificationServiceV2;
import com.whatsapp.notifications.services.WhatsAppTemplateServiceV2;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot AutoConfiguration for WhatsApp Services
 * 
 * This auto-configuration:
 * 1. Reads WhatsApp properties from application.yml
 * 2. Creates beans for template service and notification service
 * 3. Only activates when whatsapp.enabled=true
 * 
 * Configuration in application.yml:
 * ```yaml
 * whatsapp:
 *   enabled: true
 *   phone-number-id: "1018222654706712"
 *   access-token: "EAAQ0IiXx8Ms..."
 *   default-language: es
 *   templates:
 *     appointment-confirmation:
 *       languages:
 *         es: "appointment_confirmation_es"
 *         en_US: "appointment_confirmation_en_us"
 *       params: 4
 *       buttons: ["confirm", "reschedule"]
 * ```
 * 
 * No Spring configuration needed in your project - just add the properties above!
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(name = "whatsapp.enabled", havingValue = "true")
@EnableConfigurationProperties({WhatsAppProperties.class})
public class WhatsAppAutoConfiguration {

    /**
     * Creates WhatsAppClient bean from WhatsApp properties
     * This is the low-level HTTP client that communicates with WhatsApp Cloud API
     */
    @Bean
    public WhatsAppClient whatsAppClient(WhatsAppProperties props) {
        log.info("════════════════════════════════════════");
        log.info("WhatsApp Configuration Starting");
        log.info("════════════════════════════════════════");
        log.info("whatsapp.enabled: true");
        log.info("whatsapp.phone-number-id: {}", props.getPhoneNumberId());
        log.info("whatsapp.access-token: {}", props.getAccessToken() != null && !props.getAccessToken().isBlank() ? "SET (length=" + props.getAccessToken().length() + ")" : "EMPTY");
        log.info("whatsapp.max-retries: {}", props.getMaxRetries());
        log.info("════════════════════════════════════════");
        
        try {
            props.validate();
        } catch (IllegalArgumentException e) {
            log.error("FATAL: Invalid WhatsApp configuration: {}", e.getMessage());
            throw e;
        }
        
        WhatsAppClient client = new WhatsAppClient(
            props.getPhoneNumberId(),
            props.getAccessToken(),
            props.getMaxRetries()
        );
        
        log.info("✓ WhatsAppClient bean created successfully!");
        log.info("════════════════════════════════════════");
        
        return client;
    }

    /**
     * Creates WhatsAppTemplateConfig bean from WhatsAppProperties
     * Converts the Spring-managed properties into the library's POJO
     */
    @Bean
    public WhatsAppTemplateConfig whatsAppTemplateConfig(WhatsAppProperties props) {
        WhatsAppTemplateConfig config = new WhatsAppTemplateConfig();
        
        config.setDefaultLanguage(props.getDefaultLanguage());
        
        // Convert templates from properties to config format
        java.util.Map<String, WhatsAppTemplateConfig.TemplateTypeConfig> templatesMap = new java.util.HashMap<>();
        
        if (props.getTemplates() != null) {
            props.getTemplates().forEach((templateType, templateProps) -> {
                WhatsAppTemplateConfig.TemplateTypeConfig typeConfig = 
                    new WhatsAppTemplateConfig.TemplateTypeConfig(
                        templateProps.getLanguages(),
                        templateProps.getParams(),
                        templateProps.getButtons()
                    );
                templatesMap.put(templateType, typeConfig);
            });
        }
        
        config.setTemplates(templatesMap);
        
        log.info("✓ Created WhatsAppTemplateConfig bean with {} template types", 
                   templatesMap.size());
        
        return config;
    }

    /**
     * Creates WhatsAppTemplateServiceV2 bean from notifications-lib
     * 
     * This service handles:
     * - Template resolution by type and language
     * - Sending messages to WhatsApp API
     * - Parameter substitution
     */
    @Bean
    public WhatsAppTemplateServiceV2 whatsAppTemplateServiceV2(
            WhatsAppProperties props,
            WhatsAppTemplateConfig templateConfig) {
        
        log.info("");
        log.info("════════════════════════════════════════════════════════════");
        log.info("  WhatsAppTemplateServiceV2 Auto-Configuration");
        log.info("════════════════════════════════════════════════════════════");
        log.info("  Phone Number ID: {}", props.getPhoneNumberId());
        log.info("  Access Token: SET (length={})", 
                 props.getAccessToken() != null ? props.getAccessToken().length() : 0);
        log.info("  Default Language: {}", templateConfig.getDefaultLanguage());
        
        if (templateConfig.getTemplates() != null) {
            log.info("  Configured Template Types:");
            templateConfig.getTemplates().forEach((type, config) -> {
                log.info("      - {}: {} languages, {} params, {} buttons",
                         type,
                         config.getLanguages() != null ? config.getLanguages().size() : 0,
                         config.getParams(),
                         config.getButtons().size());
                if (config.getLanguages() != null) {
                    config.getLanguages().forEach((lang, templateName) -> {
                        log.info("        → {}: {}", lang, templateName);
                    });
                }
            });
        }
        log.info("════════════════════════════════════════════════════════════");
        log.info("");
        
        return new WhatsAppTemplateServiceV2(
            props.getPhoneNumberId(),
            props.getAccessToken(),
            templateConfig
        );
    }

    /**
     * Creates WhatsAppNotificationServiceV2 bean from notifications-lib
     * 
     * This is the HIGH-LEVEL service to use in your business logic.
     * 
     * Usage in any Spring service:
     * ```java
     * @Autowired
     * private WhatsAppNotificationServiceV2 whatsAppNotificationService;
     * 
     * // When creating appointment:
     * whatsAppNotificationService.sendCitaConfirmation(
     *     phone, clientName, serviceName, date, time, languageCode
     * );
     * ```
     */
    @Bean
    public WhatsAppNotificationServiceV2 whatsAppNotificationServiceV2(
            WhatsAppClient whatsAppClient,
            WhatsAppTemplateServiceV2 whatsAppTemplateServiceV2,
            WhatsAppTemplateConfig templateConfig) {
        
        log.info("✓ Created WhatsAppNotificationServiceV2 bean");
        
        return new WhatsAppNotificationServiceV2(
            whatsAppClient,
            whatsAppTemplateServiceV2,
            templateConfig
        );
    }
}
