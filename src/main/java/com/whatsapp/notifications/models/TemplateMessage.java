package com.whatsapp.notifications.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateMessage {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("language")
    private Language language;
    
    @JsonProperty("components")
    private List<Component> components;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {
        @JsonProperty("code")
        private String code = "es";
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Component {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("parameters")
        private List<Parameter> parameters;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameter {
        @JsonProperty("type")
        private String type = "text";
        
        @JsonProperty("text")
        private String text;
    }
}
