# WhatsApp Notifications Library

Una librería Java profesional para integrar notificaciones por WhatsApp usando la API Cloud de Meta con soporte completo para botones interactivos.

## ✨ Características

- ✅ **Soporte para 3 tipos de botones**: Quick Reply, URL, Phone Number
- ✅ **Templates multi-idioma**: Español, Inglés, Portugués, y más
- ✅ **Validación profesional**: En inicialización + durante envío
- ✅ **Generación automática de componentes**: Para Meta Cloud API
- ✅ **Type-safe**: Enums y configuración fuertemente tipada
- ✅ **Configuración YAML limpia**: Fácil de mantener y escalar

---

## Requisitos

- Java 17+
- Spring Boot 3.x (opcional, para auto-configuración)
- Maven 3.8+

##  Instalación

Aun no está en MAVEN-REPOSITORY

### Opción 1: Maven Local (Recomendado)

Clonar el repositorio y construir localmente:

```bash
git clone https://github.com/Carlos61116/WhatsAppLibrary.git
cd WhatsAppLibrary
mvn clean install
```

Luego agregar a tu `pom.xml`:

```xml
<dependency>
    <groupId>com.github.carlos61116</groupId>
    <artifactId>whatsapp-notifications-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Opción 2: JAR Directo (Sin Maven)

1. Descargar o compilar el JAR

E2. Copiar `target/whatsapp-notifications-lib-1.0.0.jar` a la carpeta `lib/` de tu proyecto

3. Agregar al classpath (según tu IDE/framework)

## Configuración

Revisar `application-whatsapp.example.yml`

### Configuración Básica

```yaml
whatsapp:
  enabled: true
  phone-number-id: "${WHATSAPP_PHONE_NUMBER_ID}"
  access-token: "${WHATSAPP_ACCESS_TOKEN}"
  api-version: "v22.0"
  api-base-url: "https://graph.facebook.com"
  default-language: es

  templates:
    appointment-confirmation:
      languages:
        es: "appointment_confirmation_es"
        en_US: "appointment_confirmation_en_us"
      params: 4
      buttons:
        - type: quick_reply
          index: 0
          text: "Confirmar"
          payload: "confirm"
        - type: quick_reply
          index: 1
          text: "Reprogramar"
          payload: "reschedule"
```

## Uso

### Básico: Enviar mensaje con template

```java
@Service
public class AppointmentService {
    
    @Autowired
    private WhatsAppTemplateService templateService;
    
    public void sendConfirmation(String phone, String clientName, 
                                String service, String date, String time) {
        try {
            List<String> params = Arrays.asList(clientName, service, date, time);
            String messageId = templateService.sendTemplateByType(
                phone,
                "appointment-confirmation",
                params
            );
            log.info("WhatsApp sent. Message ID: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending WhatsApp", e);
        }
    }
}
```

### Con idioma específico

```java
String messageId = templateService.sendTemplateByType(
    "+34654321098",
    "appointment-confirmation",
    Arrays.asList("Juan García", "Corte", "2026-03-20", "14:30"),
    "en_US"  // Language override
);
```

### Con botones personalizados

Al usar templates, no es necesario enviar tema de botones.

```java
List<ButtonConfig> buttons = Arrays.asList(
    new ButtonConfig("quick_reply", 0, "Confirmar", "confirm"),
    new ButtonConfig("quick_reply", 1, "Reprogramar", "reschedule")
);

templateService.sendTemplate(
    "+34654321098",
    "appointment_confirmation_es",
    "es",
    Arrays.asList("Juan", "Corte", "2026-03-20", "14:30"),
    buttons
);
```

---




## Validaciones Incluidas

✅ Máximo 3 botones por template  
✅ Índices consecutivos (0, 1, 2)  
✅ Validación de formatos (URLs HTTPS, teléfono con +país)  
✅ Conteo de parámetros correcto  
✅ Idiomas configurados existentes  
✅ Mensajes de error descriptivos  
✅ Validación en inicialización + runtime

---

## Troubleshooting

### "Invalid template configuration"
Verifica que:
- El YAML es válido
- Todos los templates tienen idiomas configurados  
- Los nombres de propiedades coinciden (type, index, text, payload, etc.)

### "WhatsApp API error 400"
- Verifica el formato del teléfono (+país + número)
- Verifica que el nombre del template es exacto en Meta Manager
- Verifica que los parámetros coinciden en cantidad y no están vacíos

### "Has X buttons, but maximum is 3"
Meta Cloud API solo permite máximo 3 botones. Crea templates separados si necesitas más funcionalidad.

### "No template for type 'X', language 'Y'"
- Agrega el idioma a la configuración del template
- O usa un idioma ya configurado
- El servicio fallará si el idioma no existe

---

## Licencia

Apache License 2.0

---

## Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request
