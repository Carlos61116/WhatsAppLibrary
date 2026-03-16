package com.whatsapp.notifications.config;

/**
 * Enum for WhatsApp button types supported by Meta Cloud API
 */
public enum ButtonType {
    /**
     * Quick reply button - sends text payload back
     * Used for simple yes/no, confirm/cancel scenarios
     */
    QUICK_REPLY("quick_reply"),
    
    /**
     * URL button - opens a web URL (can include dynamic parameters)
     */
    URL("url"),
    
    /**
     * Phone number button - initiates a call
     */
    PHONE_NUMBER("phone_number");

    /**
     * Meta API value for this button type
     */
    private final String metaValue;

    ButtonType(String metaValue) {
        this.metaValue = metaValue;
    }

    /**
     * Get the Meta Cloud API value for this button type
     * @return Meta API string value
     */
    public String getMetaValue() {
        return metaValue;
    }

    /**
     * Parse a string to ButtonType
     * @param value String value to parse
     * @return ButtonType enum value
     * @throws IllegalArgumentException if value doesn't match any button type
     */
    public static ButtonType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Button type cannot be null or blank");
        }
        
        return switch (value.toLowerCase()) {
            case "quick_reply", "quickreply" -> QUICK_REPLY;
            case "url", "link" -> URL;
            case "phone_number", "phonenumber", "phone" -> PHONE_NUMBER;
            default -> throw new IllegalArgumentException("Unknown button type: " + value);
        };
    }
}
