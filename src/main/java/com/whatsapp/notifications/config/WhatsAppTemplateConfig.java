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

    /**
     * Default language fallback (e.g., "es", "en_US")
     */
    private String defaultLanguage = "es";

    // ============ Getters & Setters ============

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

    // ============ Public Methods ============

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

    // ============ Inner Class ============

    /**
     * Configuration for a specific template type (e.g., appointment-confirmation)
     */
    public static class TemplateTypeConfig {
        
        /**
         * Template names by language code
         * Example: { "es": "appointment_confirmation_es", "en_US": "appointment_confirmation_en_us" }
         */
        private Map<String, String> languages = new HashMap<>();

        /**
         * Number of parameters expected by the template (0-4)
         */
        private int params = 0;

        /**
         * Button IDs if the template has buttons
         * Example: ["confirm", "reschedule", "cancel"]
         */
        private List<String> buttons = List.of();

        // ============ Constructor ============

        public TemplateTypeConfig() {
        }

        public TemplateTypeConfig(Map<String, String> languages, int params, List<String> buttons) {
            this.languages = languages;
            this.params = params;
            this.buttons = buttons;
        }

        // ============ Getters & Setters ============

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

        // ============ Public Methods ============

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
            
            // Try exact language match
            if (languages.containsKey(languageCode)) {
                return languages.get(languageCode);
            }
            
            // Try language without country code (e.g., "es" from "es_ES")
            String baseLanguage = languageCode.split("_")[0];
            if (languages.containsKey(baseLanguage)) {
                return languages.get(baseLanguage);
            }
            
            // Fallback to default language
            if (languages.containsKey(defaultLanguage)) {
                return languages.get(defaultLanguage);
            }
            
            // Last resort: return first available
            return languages.values().stream().findFirst().orElse(null);
        }

        /**
         * Check if this template has buttons
         */
        public boolean hasButtons() {
            return buttons != null && !buttons.isEmpty();
        }
    }
}
