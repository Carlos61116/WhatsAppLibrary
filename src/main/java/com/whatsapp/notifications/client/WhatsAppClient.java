package com.whatsapp.notifications.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.notifications.exceptions.WhatsAppException;
import com.whatsapp.notifications.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class WhatsAppClient {
    
    private static final String API_VERSION = "v22.0";
    private static final String API_BASE_URL = "https://graph.facebook.com";
    private static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9]{10,15}$";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String phoneNumberId;
    private final String accessToken;
    private final ObjectMapper objectMapper;
    private final int maxRetries;
    
    public WhatsAppClient(String phoneNumberId, String accessToken) {
        this(phoneNumberId, accessToken, MAX_RETRIES);
    }
    
    public WhatsAppClient(String phoneNumberId, String accessToken, int maxRetries) {
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.maxRetries = maxRetries;
        this.apiUrl = String.format("%s/%s/%s/messages", API_BASE_URL, API_VERSION, phoneNumberId);
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        log.info("WhatsAppClient initialized with phoneNumberId: {}", phoneNumberId);
    }
    
    public String sendTextMessage(String phoneNumber, String message) 
            throws WhatsAppException {
        
        validatePhoneNumber(phoneNumber);
        
        WhatsAppMessage msg = new WhatsAppMessage();
        msg.setPhoneNumber(formatPhoneNumber(phoneNumber));
        msg.setType("text");
        msg.setText(new TextMessage(message));
        
        return sendMessage(msg);
    }
    
    public String sendInteractiveMessage(String phoneNumber, String bodyText, 
                                        List<InteractiveButton> buttons) 
            throws WhatsAppException {
        
        return sendInteractiveMessage(phoneNumber, null, bodyText, null, buttons);
    }
    
    public String sendInteractiveMessage(String phoneNumber, String headerText,
                                        String bodyText, String footerText,
                                        List<InteractiveButton> buttons) 
            throws WhatsAppException {
        
        validatePhoneNumber(phoneNumber);
        
        if (buttons == null || buttons.isEmpty() || buttons.size() > 3) {
            throw new WhatsAppException("Buttons list must have 1-3 items");
        }
        
        WhatsAppMessage msg = new WhatsAppMessage();
        msg.setPhoneNumber(formatPhoneNumber(phoneNumber));
        msg.setType("interactive");
        
        InteractiveMessage interactive = new InteractiveMessage();
        interactive.setType("button");
        
        if (headerText != null && !headerText.isEmpty()) {
            interactive.setHeader(new InteractiveMessage.Header("text", headerText));
        }
        
        interactive.setBody(new InteractiveMessage.Body(bodyText));
        
        if (footerText != null && !footerText.isEmpty()) {
            interactive.setFooter(new InteractiveMessage.Footer(footerText));
        }
        
        List<InteractiveMessage.Action.ActionButton> actionButtons = new ArrayList<>();
        for (InteractiveButton btn : buttons) {
            InteractiveMessage.Action.ActionButton.ButtonReply reply = 
                new InteractiveMessage.Action.ActionButton.ButtonReply(btn.getId(), btn.getTitle());
            InteractiveMessage.Action.ActionButton actionBtn = 
                new InteractiveMessage.Action.ActionButton("reply", reply);
            actionButtons.add(actionBtn);
        }
        
        InteractiveMessage.Action action = new InteractiveMessage.Action();
        action.setButtons(actionButtons);
        interactive.setAction(action);
        
        msg.setInteractive(interactive);
        return sendMessage(msg);
    }
    
    public String sendTemplateMessage(String phoneNumber, String templateName, 
                                      String... parameters) 
            throws WhatsAppException {
        
        validatePhoneNumber(phoneNumber);
        
        WhatsAppMessage msg = new WhatsAppMessage();
        msg.setPhoneNumber(formatPhoneNumber(phoneNumber));
        msg.setType("template");
        
        TemplateMessage template = new TemplateMessage();
        template.setName(templateName);
        template.setLanguage(new TemplateMessage.Language("es"));
        
        if (parameters.length > 0) {
            List<TemplateMessage.Parameter> params = new ArrayList<>();
            for (String param : parameters) {
                params.add(new TemplateMessage.Parameter("text", param));
            }
            
            TemplateMessage.Component component = new TemplateMessage.Component();
            component.setType("body");
            component.setParameters(params);
            template.setComponents(List.of(component));
        }
        
        msg.setTemplate(template);
        return sendMessage(msg);
    }
    
    private String sendMessage(WhatsAppMessage message) throws WhatsAppException {
        
        int attempt = 0;
        WhatsAppException lastException = null;
        
        while (attempt < maxRetries) {
            try {
                attempt++;
                log.debug("Sending message (attempt {}/{}): {}", attempt, maxRetries, message.getPhoneNumber());
                
                String json = objectMapper.writeValueAsString(message);
                
                HttpEntity<String> request = createHttpEntity(json);
                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Message sent successfully to: {} (Response: {})", 
                             message.getPhoneNumber(), response.getBody());
                    return response.getBody();
                } else {
                    String errorMsg = String.format("HTTP %s: %s", response.getStatusCode(), response.getBody());
                    throw new WhatsAppException(errorMsg, String.valueOf(response.getStatusCode().value()),
                                               response.getStatusCode().value());
                }
                
            } catch (WhatsAppException e) {
                lastException = e;
                
                if (e.getHttpStatusCode() != null && e.getHttpStatusCode() >= 500 && attempt < maxRetries) {
                    log.warn("Server error, retrying in {}ms (attempt {}/{})", 
                             RETRY_DELAY_MS, attempt, maxRetries);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new WhatsAppException("Interrupted while retrying", ie);
                    }
                } else {
                    throw e;
                }
                
            } catch (RestClientException e) {
                log.error("HTTP error sending message", e);
                lastException = new WhatsAppException("Failed to send message: " + e.getMessage(), e);
                
                if (attempt < maxRetries) {
                    log.warn("Connection error, retrying in {}ms", RETRY_DELAY_MS);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new WhatsAppException("Interrupted while retrying", ie);
                    }
                }
                
            } catch (Exception e) {
                log.error("Unexpected error sending message", e);
                throw new WhatsAppException("Error processing message: " + e.getMessage(), e);
            }
        }
        
        if (lastException != null) {
            throw lastException;
        }
        
        throw new WhatsAppException("Failed to send message after " + maxRetries + " attempts");
    }
    
    private HttpEntity<String> createHttpEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(json, headers);
    }
    
    private void validatePhoneNumber(String phoneNumber) throws WhatsAppException {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new WhatsAppException("Phone number cannot be empty");
        }
        
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        if (!Pattern.matches(PHONE_NUMBER_PATTERN, cleaned)) {
            throw new WhatsAppException("Invalid phone number format: " + phoneNumber);
        }
    }
    
    private String formatPhoneNumber(String phone) {
        phone = phone.replaceAll("[^0-9+]", "");
        
        if (!phone.startsWith("+")) {
            if (!phone.startsWith("34") && !phone.startsWith("1") && 
                !phone.startsWith("52") && !phone.startsWith("55")) {
                phone = "+34" + phone;
            } else {
                phone = "+" + phone;
            }
        }
        
        return phone;
    }
}
