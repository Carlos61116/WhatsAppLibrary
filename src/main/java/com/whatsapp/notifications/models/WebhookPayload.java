package com.whatsapp.notifications.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookPayload {
    
    @JsonProperty("object")
    private String object;
    
    @JsonProperty("entry")
    private List<Entry> entry;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("changes")
        private List<Change> changes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Change {
        @JsonProperty("field")
        private String field;
        
        @JsonProperty("value")
        private Value value;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        
        @JsonProperty("metadata")
        private Metadata metadata;
        
        @JsonProperty("messages")
        private List<Message> messages;
        
        @JsonProperty("statuses")
        private List<Status> statuses;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("from")
        private String from;
        
        @JsonProperty("timestamp")
        private String timestamp;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("text")
        private Text text;
        
        @JsonProperty("button")
        private Button button;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Text {
            @JsonProperty("body")
            private String body;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Button {
            @JsonProperty("text")
            private String text;
            
            @JsonProperty("payload")
            private String payload;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("timestamp")
        private String timestamp;
        
        @JsonProperty("recipient_id")
        private String recipientId;
        
        @JsonProperty("errors")
        private List<StatusError> errors;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusError {
        @JsonProperty("code")
        private Integer code;
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("message")
        private String message;
    }
}
