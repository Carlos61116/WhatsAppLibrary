package com.whatsapp.notifications.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.notifications.config.WhatsAppTemplateConfig;
import com.whatsapp.notifications.config.WhatsAppTemplateConfig.TemplateTypeConfig;

/**
 * Enhanced WhatsApp Template Service with multi-language and button support
 * 
 * This service resolves templates from configuration and sends them with proper parameters and buttons.
 * 
 * @author WhatsApp Notifications Library
 */
public class WhatsAppTemplateServiceV2 {

    private final String phoneNumberId;
    private final String accessToken;
    private final WhatsAppTemplateConfig templateConfig;
    
    private static final String API_BASE_URL = "https://graph.facebook.com/v18.0";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for WhatsAppTemplateServiceV2
     *
     * @param phoneNumberId Phone Number ID from Meta WhatsApp Manager
     * @param accessToken   Access token for Meta Graph API
     * @param templateConfig Configuration for all templates
     */
    public WhatsAppTemplateServiceV2(String phoneNumberId, String accessToken, 
                                     WhatsAppTemplateConfig templateConfig) {
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.templateConfig = templateConfig;
        
        if (phoneNumberId == null || phoneNumberId.isBlank()) {
        }
        if (accessToken == null || accessToken.isBlank()) {
        }
    }

    /**
     * Sends a template message by template type and language, automatically resolving template name
     *
     * @param targetPhoneNumber Phone number to send to (without +, e.g., "34647918823")
     * @param templateType      Template type as defined in config (e.g., "appointment-confirmation")
     * @param languageCode      Language code (e.g., "es", "en_US")
     * @param parameters        List of parameters to replace in template (ordered)
     * @return messageId from WhatsApp API
     * @throws Exception if sending fails
     */
    public String sendTemplateByType(String targetPhoneNumber, String templateType, 
                                     String languageCode, List<String> parameters) throws Exception {
    	
    	TemplateTypeConfig template = templateConfig.getTemplateType(templateType);
        // 1. Validate template type exists in config
        if (templateConfig == null || template == null) {
            throw new IllegalArgumentException("Template type not found in config: " + templateType);
        }
        
        
        // 2. Resolve template name by language (with fallback)
        String templateName = templateConfig.getTemplateName(templateType, languageCode);
        if (templateName == null) {
            throw new IllegalArgumentException(
                "No template found for type '" + templateType + "' and language '" + languageCode + "'");
        }
       

        // 3. Validate each parameter is not null/empty
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                String param = parameters.get(i);
                if (param == null || param.trim().isEmpty()) {
                    throw new IllegalArgumentException(
                        String.format("Parameter at index %d is null or empty for template '%s'",
                            i, templateType));
                }
            }
        }
        
        List<String> buttons = template.getButtons();

        // 4. Delegate to lower-level send method with validated parameters
        return sendTemplate(targetPhoneNumber, templateName, languageCode, parameters, buttons);
    }

    /**
     * Sends a template message (lower-level, for direct template name usage)
     *
     * @param targetPhoneNumber Phone number to send to
     * @param templateName      Template name exactly as defined in Meta
     * @param languageCode      Language code
     * @param parameters        List of parameters
     * @return messageId
     * @throws Exception if sending fails
     */
    public String sendTemplate(String targetPhoneNumber, String templateName, 
                               String languageCode, List<String> parameters,List<String> buttons) throws Exception {
        
        if (phoneNumberId == null || phoneNumberId.isBlank()) {
            throw new IllegalStateException("whatsapp.phone-number-id is not configured");
        }

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("whatsapp.access-token is not configured");
        }

        // Build request JSON
        Map<String, Object> requestBody = buildTemplateRequest(
            targetPhoneNumber, templateName, languageCode, parameters, buttons
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // Create HTTP request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(API_BASE_URL + "/" + phoneNumberId + "/messages"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        // Execute request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse response
        if (response.statusCode() == 200) {
            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
            
            if (responseMap.containsKey("messages")) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> messages = (List<Map<String, String>>) responseMap.get("messages");
                if (!messages.isEmpty()) {
                    String messageId = messages.get(0).get("id");
                                        return messageId;
                }
            }
            
            throw new RuntimeException("Unexpected WhatsApp API response format");
        } else {
            throw new RuntimeException("WhatsApp API returned status " + response.statusCode() + ": " + response.body());
        }
    }

    /**
     * Builds the JSON request body for WhatsApp template API with parameters and buttons
     */
    private Map<String, Object> buildTemplateRequest(String targetPhoneNumber, String templateName,
                                                     String languageCode, List<String> parameters, List<String> buttons) {
        Map<String, Object> request = new HashMap<>();
        request.put("messaging_product", "whatsapp");
        request.put("recipient_type", "individual");
        request.put("to", targetPhoneNumber);
        request.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);

        Map<String, String> language = new HashMap<>();
        language.put("code", languageCode);
        template.put("language", language);
        List<Map<String, Object>> components = new ArrayList<>();
        // Add components (parameters) if they exist
        if (parameters != null && !parameters.isEmpty()) {
            
            
            Map<String, Object> bodyComponent = new HashMap<>();
            bodyComponent.put("type", "body");
            
            List<Map<String, Object>> bodyParameters = new ArrayList<>();
            for (String param : parameters) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("type", "text");
                paramMap.put("text", param);
                bodyParameters.add(paramMap);
            }
            
            bodyComponent.put("parameters", bodyParameters);
            components.add(bodyComponent);
            template.put("components", components);
        }
        
        if (buttons != null && !buttons.isEmpty()) {
            
            
            Map<String, Object> buttonComponent = new HashMap<>();
            buttonComponent.put("type", "button");
            buttonComponent.put("sub_type", "quick_reply");
            buttonComponent.put("index", "0");
            buttonComponent.put("type", "button");
            
            List<Map<String, Object>> buttonParameters = new ArrayList<>();
            for (String param : buttons) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("type", "payload");
                paramMap.put("payload", param);
                buttonParameters.add(paramMap);
            }
            
            buttonComponent.put("parameters", buttonParameters);
            components.add(buttonComponent);
            template.put("components", components);
        }

        request.put("template", template);
        return request;
    }

    /**
     * Checks if the service is properly configured
     */
    public boolean isConfigured() {
        return phoneNumberId != null && !phoneNumberId.isBlank() && 
               accessToken != null && !accessToken.isBlank() &&
               templateConfig != null && !templateConfig.getTemplates().isEmpty();
    }
}
