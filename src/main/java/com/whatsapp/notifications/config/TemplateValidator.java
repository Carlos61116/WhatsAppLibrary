package com.whatsapp.notifications.config;

import java.util.ArrayList;
import java.util.List;


/**
 * Validates WhatsApp template configuration from YAML
 * 
 * Note: Buttons are NOT validated here. They are:
 * - Pre-defined in Meta Business Manager
 * - NOT sent in the JSON payload
 * - Optional documentation in YAML only
 */
public class TemplateValidator {
    
    public static List<String> validate(WhatsAppTemplateConfig config) {
        List<String> errors = new ArrayList<>();
        
        if (config == null) {
            errors.add("Template configuration cannot be null");
            return errors;
        }
        
        if (config.getTemplates() == null || config.getTemplates().isEmpty()) {
            errors.add("No templates defined in configuration");
            return errors;
        }
        
        // Validate each template type
        config.getTemplates().forEach((templateType, templateConfig) -> {
            errors.addAll(validateTemplateType(templateType, templateConfig));
        });
        
        return errors;
    }
    
    public static List<String> validateTemplateType(String templateType, 
                                                     WhatsAppTemplateConfig.TemplateTypeConfig config) {
        List<String> errors = new ArrayList<>();
        
        if (config == null) {
            errors.add("Template '" + templateType + "' configuration is null");
            return errors;
        }
        
        // Validate languages (required)
        if (config.getLanguages() == null || config.getLanguages().isEmpty()) {
            errors.add("Template '" + templateType + "' has no language configurations");
        }
        
        // Validate parameters (non-negative integer)
        if (config.getParams() < 0) {
            errors.add("Template '" + templateType + "' has invalid params count: " + config.getParams());
        }
        
        return errors;
    }
    
    public static boolean isValid(WhatsAppTemplateConfig config) {
        return validate(config).isEmpty();
    }
    
    /**
     * Check if a template type is valid
     * @param templateType Template type name
     * @param config Template configuration
     * @return true if valid, false otherwise
     */
    public static boolean isValidTemplateType(String templateType, 
                                             WhatsAppTemplateConfig.TemplateTypeConfig config) {
        return validateTemplateType(templateType, config).isEmpty();
    }
}
