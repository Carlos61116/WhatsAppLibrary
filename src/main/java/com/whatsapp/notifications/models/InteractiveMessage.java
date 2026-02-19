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
public class InteractiveMessage {
    
    @JsonProperty("type")
    private String type = "button";
    
    @JsonProperty("header")
    private Header header;
    
    @JsonProperty("body")
    private Body body;
    
    @JsonProperty("footer")
    private Footer footer;
    
    @JsonProperty("action")
    private Action action;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Header {
        @JsonProperty("type")
        private String type = "text";
        
        @JsonProperty("text")
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        @JsonProperty("text")
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Footer {
        @JsonProperty("text")
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Action {
        @JsonProperty("buttons")
        private List<ActionButton> buttons;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ActionButton {
            @JsonProperty("type")
            private String type = "reply";
            
            @JsonProperty("reply")
            private ButtonReply reply;
            
            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class ButtonReply {
                @JsonProperty("id")
                private String id;
                
                @JsonProperty("title")
                private String title;
            }
        }
    }
}
