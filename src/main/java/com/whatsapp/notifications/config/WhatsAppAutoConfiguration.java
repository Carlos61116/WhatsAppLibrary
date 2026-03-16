package com.whatsapp.notifications.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.whatsapp.notifications.client.WhatsAppClient;
import com.whatsapp.notifications.services.WhatsAppTemplateService;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(name = "whatsapp.enabled", havingValue = "true")
@EnableConfigurationProperties({WhatsAppProperties.class})
public class WhatsAppAutoConfiguration {

    @Bean
     WhatsAppClient whatsAppClient(WhatsAppProperties props) {
        props.validate();
        WhatsAppClient client = new WhatsAppClient(
            props.getPhoneNumberId(),
            props.getAccessToken(),
            props.getApiVersion(),
            props.getApiBaseUrl(),
            props.getPhoneNumberPattern(),
            props.getMaxRetries(),
            props.getRetryDelayMs()
        );
        log.info("WhatsApp client configured");
        return client;
    }

    @Bean
     WhatsAppTemplateConfig whatsAppTemplateConfig(WhatsAppProperties props) {
        WhatsAppTemplateConfig config = new WhatsAppTemplateConfig();
        config.setDefaultLanguage(props.getDefaultLanguage());
        
        Map<String, WhatsAppTemplateConfig.TemplateTypeConfig> templates = new HashMap<>();
        if (props.getTemplates() != null) {
            props.getTemplates().forEach((type, templateProps) -> {
                WhatsAppTemplateConfig.TemplateTypeConfig typeConfig = 
                    new WhatsAppTemplateConfig.TemplateTypeConfig(
                        templateProps.getLanguages(),
                        templateProps.getParams(),
                        templateProps.getButtons()
                    );
                templates.put(type, typeConfig);
            });
        }
        config.setTemplates(templates);
        
        log.info("WhatsApp templates configured: {} types", templates.size());
        return config;
    }

    @Bean
    WhatsAppTemplateService whatsAppTemplateService(WhatsAppProperties props,
                                                 WhatsAppTemplateConfig templateConfig) {
        return new WhatsAppTemplateService(
            props.getPhoneNumberId(),
            props.getAccessToken(),
            props.getApiBaseUrl(),
            props.getApiVersion(),
            templateConfig
        );
    }

}
