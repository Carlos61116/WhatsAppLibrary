package com.whatsapp.notifications.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessage {
    
    @JsonProperty("messaging_product")
    private String messagingProduct = "whatsapp";
    
    @JsonProperty("to")
    private String phoneNumber;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("text")
    private TextMessage text;
    
    @JsonProperty("template")
    private TemplateMessage template;
   
    
    public WhatsAppMessage(String phoneNumber, TextMessage text) {
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.type = "text";
    }
    
    public WhatsAppMessage(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.type = "interactive";
    }
}
