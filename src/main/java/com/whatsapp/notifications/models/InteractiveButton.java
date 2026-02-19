package com.whatsapp.notifications.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveButton {
    
    private String id;
    private String title;
    
    public static InteractiveButton confirm(String id) {
        return new InteractiveButton(id, "Confirmar");
    }
    
    public static InteractiveButton cancel(String id) {
        return new InteractiveButton(id, "Cancelar");
    }
    
    public static InteractiveButton reschedule(String id) {
        return new InteractiveButton(id, "Reagendar");
    }
}
