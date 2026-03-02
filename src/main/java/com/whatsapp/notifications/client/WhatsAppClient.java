package com.whatsapp.notifications.client;

import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WhatsAppClient {
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String phoneNumberId;
    private final String accessToken;
    private final ObjectMapper objectMapper;
    private final int maxRetries;
    private final long retryDelayMs;
    private final Pattern phoneNumberValidator;
    
    public WhatsAppClient(String phoneNumberId, String accessToken, String apiVersion, 
                         String apiBaseUrl, String phoneNumberPattern, int maxRetries, long retryDelayMs) {
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
        this.phoneNumberValidator = Pattern.compile(phoneNumberPattern);
        this.apiUrl = String.format("%s/%s/%s/messages", apiBaseUrl, apiVersion, phoneNumberId);
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        log.debug("WhatsApp client: {} (max-retries={}, delay={}ms)", 
                  phoneNumberId, maxRetries, retryDelayMs);
    }

    public WhatsAppClient(String phoneNumberId, String accessToken, String apiVersion, 
                         String apiBaseUrl, String phoneNumberPattern) {
        this(phoneNumberId, accessToken, apiVersion, apiBaseUrl, phoneNumberPattern, 3, 1000);
    }
    
    
    public Pattern getPhoneNumberValidator() {
        return phoneNumberValidator;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public long getRetryDelayMs() {
        return retryDelayMs;
    }
}
