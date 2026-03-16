package com.whatsapp.notifications.config;

public class ButtonConfig {
    
    private String type;
    
    private int index;
    
    private String text;
    
    private String payload;
    
    private String url;

    private boolean dynamic = false;

    private String phoneNumber;

    
    public ButtonConfig() {}

    public ButtonConfig(String type, int index) {
        this.type = type;
        this.index = index;
    }

    public ButtonConfig(String type, int index, String text, String payload) {
        this.type = type;
        this.index = index;
        this.text = text;
        this.payload = payload;
    }

    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ButtonType getButtonType() {
        return ButtonType.fromString(this.type);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ButtonType asButtonType() {
        return ButtonType.fromString(this.type);
    }

    public boolean isQuickReply() {
        return ButtonType.QUICK_REPLY.name().equalsIgnoreCase(type) || 
               "quick_reply".equalsIgnoreCase(type);
    }

    public boolean isUrl() {
        return ButtonType.URL.name().equalsIgnoreCase(type) || 
               "url".equalsIgnoreCase(type);
    }

    public boolean isPhoneNumber() {
        return ButtonType.PHONE_NUMBER.name().equalsIgnoreCase(type) || 
               "phone_number".equalsIgnoreCase(type);
    }

}
