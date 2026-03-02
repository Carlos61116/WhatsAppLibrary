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
 * Enhanced WhatsApp Template Service with multi-language and button support (WIP)
 * 
 * This service resolves templates from configuration and sends them with proper parameters and buttons.
 * 
 * @author WhatsApp Notifications Library
 */
public class WhatsAppTemplateService {

    private final String phoneNumberId;
    private final String accessToken;
    private final WhatsAppTemplateConfig templateConfig;
    private final String apiUrl;
    
    private static final ObjectMapper mapper = new ObjectMapper();

    public WhatsAppTemplateService(String phoneNumberId, String accessToken, 
                                     String apiBaseUrl, String apiVersion,
                                     WhatsAppTemplateConfig templateConfig) {
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.apiUrl = String.format("%s/%s/%s/messages", apiBaseUrl, apiVersion, phoneNumberId);
        this.templateConfig = templateConfig;
        
        if (phoneNumberId == null || phoneNumberId.isBlank()) {
            throw new IllegalArgumentException("phoneNumberId cannot be null or blank");
        }
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken cannot be null or blank");
        }
    }
    
    
    
    

    public String sendTemplateByType(String targetPhone, String templateType,List<String> parameters) throws Exception {
        TemplateTypeConfig template = templateConfig.getTemplateType(templateType);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateType);
        }
        
        String languageCode = templateConfig.getDefaultLanguage();
        
        String templateName = templateConfig.getTemplateName(templateType, languageCode);
        if (templateName == null) {
            throw new IllegalArgumentException(
                String.format("No template for type '%s', language '%s'", templateType, languageCode));
        }
        
        validateParameters(parameters, templateType);
        return sendTemplate(targetPhone, templateName, languageCode, parameters, template.getButtons());
    }

    public String sendTemplate(String targetPhone, String templateName, String languageCode,
                               List<String> parameters, List<String> buttons) throws Exception {
        validateCredentials();        
        Map<String, Object> payload = buildPayload(targetPhone, templateName, languageCode, parameters, buttons);
        return sendRequest(payload);
    }

    private void validateCredentials() {
        if (phoneNumberId == null || phoneNumberId.isBlank()) {
            throw new IllegalStateException("phoneNumberId not configured");
        }
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("accessToken not configured");
        }
    }
    
    private void validateParameters(List<String> parameters, String templateType) {
        if (parameters == null) return;
        for (int i = 0; i < parameters.size(); i++) {
            String param = parameters.get(i);
            if (param == null || param.isBlank()) {
                throw new IllegalArgumentException(
                    String.format("Parameter %d is empty for template '%s'", i, templateType));
            }
        }
    }
    
    private String sendRequest(Map<String, Object> payload) throws Exception {
        String json = mapper.writeValueAsString(payload);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(apiUrl))
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                String.format("WhatsApp API error %d: %s", response.statusCode(), response.body()));
        }
        
        return extractMessageId(response.body());
    }
    
    private String extractMessageId(String responseBody) throws Exception {
        Map<String, Object> response = mapper.readValue(responseBody, Map.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) response.get("messages");
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).get("id");
        }
        throw new RuntimeException("No messageId in response");
    }

    private Map<String, Object> buildPayload(String targetPhone, String templateName,
                                              String languageCode, List<String> parameters, List<String> buttons) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", targetPhone);
        payload.put("type", "template");
        
        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", languageCode));
        
        List<Map<String, Object>> components = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            components.add(buildBodyComponent(parameters));
        }
        if (buttons != null && !buttons.isEmpty()) {
            components.add(buildButtonComponent(buttons));
        }
        
        if (!components.isEmpty()) {
            template.put("components", components);
        }
        
        payload.put("template", template);
        return payload;
    }
    
    private Map<String, Object> buildBodyComponent(List<String> parameters) {
        Map<String, Object> component = new HashMap<>();
        component.put("type", "body");
        
        List<Map<String, Object>> params = new ArrayList<>();
        for (String param : parameters) {
            params.add(Map.of("type", "text", "text", param));
        }
        component.put("parameters", params);
        return component;
    }
    
    private Map<String, Object> buildButtonComponent(List<String> buttons) {
        Map<String, Object> component = new HashMap<>();
        component.put("type", "button");
        component.put("sub_type", "quick_reply");
        
        //TODO Make different buttons types
        List<Map<String, Object>> params = new ArrayList<>();
        for (String btn : buttons) {
            params.add(Map.of("type", "payload", "payload", btn));
        }
        component.put("parameters", params);
        return component;
    }
    
    public boolean isConfigured() {
        return phoneNumberId != null && !phoneNumberId.isBlank() && 
               accessToken != null && !accessToken.isBlank();
    }
}
