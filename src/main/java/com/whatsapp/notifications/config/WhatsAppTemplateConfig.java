package com.whatsapp.notifications.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for WhatsApp templates with multi-language support and buttons
 * 
 * Maps from application-whatsapp.yml (handled by backend's Spring configuration):
 * whatsapp:
 *   templates:
 *     default-language: es
 *     appointment-confirmation:
 *       languages:
 *         es: appointment_confirmation_es
 *         en_US: appointment_confirmation_en_us
 *       params: 4
 *       buttons: [confirm, reschedule, call]
 * 
 * This is a pure POJO - Spring property binding is configured in the backend,
 * not in the library itself, to avoid tight coupling with Spring.
 */
public class WhatsAppTemplateConfig {

    /**
     * All template definitions by type (e.g., "appointment-confirmation", "appointment-reminder")
     */
    private Map<String, TemplateTypeConfig> templates = new HashMap<>();


    private String defaultLanguage = "es";


    public Map<String, TemplateTypeConfig> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, TemplateTypeConfig> templates) {
        this.templates = templates;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }


    /**
     * Get template configuration by type
     * 
     * @param templateType Template type (e.g., "appointment-confirmation")
     * @return TemplateTypeConfig or null if not found
     */
    public TemplateTypeConfig getTemplateType(String templateType) {
        return templates != null ? templates.get(templateType) : null;
    }

    /**
     * Get template name for a specific type and language
     * 
     * @param templateType Template type (e.g., "appointment-confirmation")
     * @param languageCode Language code (e.g., "es", "en_US")
     * @return Template name or null if not found
     */
    public String getTemplateName(String templateType, String languageCode) {
        TemplateTypeConfig config = getTemplateType(templateType);
        if (config == null) {
            return null;
        }
        return config.getTemplateName(languageCode, defaultLanguage);
    }

    /**
     * Check if a template configuration exists
     * 
     * @param templateType Template type to check
     * @return true if template type is configured
     */
    public boolean hasTemplateType(String templateType) {
        return templates != null && templates.containsKey(templateType);
    }


    /**
     * Configuration for a specific template type (e.g., appointment-confirmation)
     */
    public static class TemplateTypeConfig {
        
        private Map<String, String> languages = new HashMap<>();

        private int params = 0;

        private List<String> buttons = List.of();


        public TemplateTypeConfig() {
        }

        public TemplateTypeConfig(Map<String, String> languages, int params, List<String> buttons) {
            this.languages = languages;
            this.params = params;
            this.buttons = buttons;
        }


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

        public List<String> getButtons() {
            return buttons;
        }

        public void setButtons(List<String> buttons) {
            this.buttons = buttons;
        }


        /**
         * Get template name for a specific language, with fallback to default
         * 
         * @param languageCode Language to lookup (e.g., "es", "en_US")
         * @param defaultLanguage Fallback language if not found
         * @return Template name or null if not found
         */
        public String getTemplateName(String languageCode, String defaultLanguage) {
            if (languages == null || languages.isEmpty()) {
                return null;
            }
            
            if (languages.containsKey(languageCode)) {
                return languages.get(languageCode);
            }
            
            String baseLanguage = languageCode.split("_")[0];
            if (languages.containsKey(baseLanguage)) {
                return languages.get(baseLanguage);
            }
            
            if (languages.containsKey(defaultLanguage)) {
                return languages.get(defaultLanguage);
            }
            
            return languages.values().stream().findFirst().orElse(null);
        }

        public boolean hasButtons() {
            return buttons != null && !buttons.isEmpty();
        }
    }
}
