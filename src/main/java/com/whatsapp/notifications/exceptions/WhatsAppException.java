package com.whatsapp.notifications.exceptions;

public class WhatsAppException extends Exception {
    
    private String errorCode;
    private Integer httpStatusCode;
    
    public WhatsAppException(String message) {
        super(message);
    }
    
    public WhatsAppException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WhatsAppException(String message, String errorCode, Integer httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}
