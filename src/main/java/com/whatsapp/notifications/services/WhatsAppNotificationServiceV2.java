package com.whatsapp.notifications.services;

import java.util.List;

import com.whatsapp.notifications.client.WhatsAppClient;
import com.whatsapp.notifications.config.WhatsAppTemplateConfig;
import com.whatsapp.notifications.exceptions.WhatsAppException;

/**
 * Enhanced WhatsApp Notification Service with multi-language and multi-template support
 * 
 * This service provides type-safe methods for sending various notifications, automatically
 * resolving the correct template based on language and notification type.
 * 
 * Each notification type can have:
 * - Multiple language versions
 * - Different parameters
 * - Optional buttons
 * 
 * @author WhatsApp Notifications Library
 */
public class WhatsAppNotificationServiceV2 {


    private final WhatsAppClient whatsAppClient;
    private final WhatsAppTemplateServiceV2 whatsAppTemplateService;
    private final WhatsAppTemplateConfig templateConfig;

    /**
     * Constructor
     *
     * @param whatsAppClient WhatsAppClient instance (can be null if not configured)
     * @param whatsAppTemplateService WhatsAppTemplateServiceV2 instance
     * @param templateConfig Configuration for all templates
     */
    public WhatsAppNotificationServiceV2(
            WhatsAppClient whatsAppClient,
            WhatsAppTemplateServiceV2 whatsAppTemplateService,
            WhatsAppTemplateConfig templateConfig) {
        this.whatsAppClient = whatsAppClient;
        this.whatsAppTemplateService = whatsAppTemplateService;
        this.templateConfig = templateConfig;
    }

    /**
     * Sends appointment confirmation with template (or text fallback)
     * Template parameters: clientName, serviceName, date, time
     *
     * @param clientPhone Phone number of the client
     * @param clientName Name of the client
     * @param serviceName Name of the service
     * @param fecha Date in format dd/MM/yyyy
     * @param hora Time in format HH:mm
     * @param templateType Template type identifier (e.g., "hello_world") - MUST come from backend configuration
     * @param languageCode Language code (e.g., "es", "en_US") - uses default if not available
     */
    public void sendTemplateConfirmation(String clientPhone,List<String> params,
                                     String templateType) {
        
        String phoneNumber = cleanPhoneNumber(clientPhone);
        
        sendTemplateDirectly(
            phoneNumber,
            templateType,
            params
        );
    }
    /**
     * Sends appointment reminder
     * Template parameters: clientName, time
     * 
     * @param templateType Template type identifier (e.g., "appointment-reminder") - MUST come from backend configuration
     */
    private void sendCitaReminder(String clientPhone, String clientName, String hora,
                                 String templateType, String languageCode) {
        
        String phoneNumber = cleanPhoneNumber(clientPhone);
        List<String> params = List.of(clientName, hora);
        
        sendTemplateWithFallback(
            phoneNumber,
            templateType,
            languageCode,
            params,
            () -> sendTextMessage(clientPhone, buildCitaReminderText(clientName, hora))
        );
    }

    /**
     * Sends appointment reminder (defaults to service language if not specified)
     */
    public void sendCitaReminder(String clientPhone, String clientName, String hora,
                                 String templateType) {
        sendCitaReminder(clientPhone, clientName, hora, templateType, null);
    }

    /**
     * Sends no-show notification
     * 
     * @param templateType Template type identifier (e.g., "no-show") - MUST come from backend configuration
     */
    public void sendNoShowNotification(String clientPhone, String clientName, String templateType, String languageCode) {
        String phoneNumber = cleanPhoneNumber(clientPhone);
        
        sendTemplateWithFallback(
            phoneNumber,
            templateType,
            languageCode,
            List.of(clientName),
            () -> sendTextMessage(clientPhone, buildNoShowText(clientName))
        );
    }

    /**
     * Sends no-show notification (defaults to service language if not specified)
     */
    public void sendNoShowNotification(String clientPhone, String clientName, String templateType) {
        sendNoShowNotification(clientPhone, clientName, templateType, null);
    }

    /**
     * Sends post-appointment thank you message
     * 
     * @param templateType Template type identifier (e.g., "post-appointment") - MUST come from backend configuration
     */
    public void sendPostCitaNotification(String clientPhone, String clientName, String templateType, String languageCode) {
        String phoneNumber = cleanPhoneNumber(clientPhone);
        
        sendTemplateWithFallback(
            phoneNumber,
            templateType,
            languageCode,
            List.of(clientName),
            () -> sendTextMessage(clientPhone, buildPostCitaText(clientName))
        );
    }

    /**
     * Sends post-appointment thank you message (defaults to service language if not specified)
     */
    public void sendPostCitaNotification(String clientPhone, String clientName, String templateType) {
        sendPostCitaNotification(clientPhone, clientName, templateType, null);
    }

    /**
     * Generic method to send template with automatic fallback
     * Tries to send template first, falls back to text if that fails
     */
    private void sendTemplateWithFallback(String phoneNumber, String templateType, 
                                         String languageCode, List<String> params,
                                         Runnable textFallback) {
        
        if (whatsAppTemplateService == null) {
            textFallback.run();
            return;
        }

        try {
            String language = languageCode != null ? languageCode : 
                             (templateConfig != null ? templateConfig.getDefaultLanguage() : "es");
            
            whatsAppTemplateService.sendTemplateByType(phoneNumber, templateType, language, params);

        } catch (Exception e) {

            textFallback.run();
        }
    }

    /**
     * Generic method to send template without fallback
     */
    private void sendTemplateDirectly(String phoneNumber, String templateType, List<String> params) {
        if (whatsAppTemplateService == null) {
            return;
        }

        try {
            String language =    (templateConfig != null ? templateConfig.getDefaultLanguage() : "es");
            
            whatsAppTemplateService.sendTemplateByType(phoneNumber, templateType, language, params);
        } catch (Exception e) {
        }
    }

    /**
     * Sends a text message (fallback for when templates fail)
     */
    private void sendTextMessage(String clientPhone, String message) {
        if (whatsAppClient == null) {
            return;
        }

        try {
            whatsAppClient.sendTextMessage(clientPhone, message);
        } catch (WhatsAppException e) {
        }
    }

    /**
     * Utility method to clean phone numbers
     */
    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return "";
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    /**
     * Utility method to check if configured
     */
    public boolean isWhatsAppConfigured() {
        boolean templateServiceAvailable = whatsAppTemplateService != null && whatsAppTemplateService.isConfigured();
        boolean clientServiceAvailable = whatsAppClient != null;
        
        boolean configured = templateServiceAvailable || clientServiceAvailable;
        
        
        return configured;
    }

    // ============ Text Message Templates (for fallback) ============

    private String buildCitaConfirmationText(String clientName, String serviceName, String fecha, String hora) {
        return String.format(
            "Hola %s!\n\n" +
            "Te hemos reservado una cita para:\n\n" +
            "*Servicio:* %s\n" +
            "*Fecha:* %s\n" +
            "*Hora:* %s\n\n" +
            "Confirmas tu asistencia?",
            clientName, serviceName, fecha, hora
        );
    }

    private String buildCitaReminderText(String clientName, String hora) {
        return String.format(
            "Hola %s\n\n" +
            "Recordatorio de tu cita:\n\n" +
            "*Manana a las* %s\n\n" +
            "Si necesitas cambios, avisanos cuanto antes.",
            clientName, hora
        );
    }

    private String buildNoShowText(String clientName) {
        return "Hola " + clientName + ",\n\n" +
               "Notamos que no asististe a tu cita.\n" +
               "Si fue un error o necesitas reagendar, contactanos.";
    }

    private String buildPostCitaText(String clientName) {
        return "Gracias " + clientName + "!\n\n" +
               "Esperamos te haya gustado el servicio.\n\n" +
               "Si tienes sugerencias, no dudes en escribir.\n" +
               "Te esperamos pronto!";
    }
}
