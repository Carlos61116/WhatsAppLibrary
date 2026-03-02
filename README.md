# WhatsApp Notifications Library

Una librería Java para integrar notificaciones por WhatsApp usando la API Cloud de Meta. Unicamente envia mensaje por templates

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

2. Copiar `target/whatsapp-notifications-lib-1.0.0.jar` a la carpeta `lib/` de tu proyecto

3. Agregar al classpath (según tu IDE/framework)

## Configuración

Revisar application-whatsapp.example

## Uso

```java
@Service
public class AppointmentService {
    
    @Autowired
    private WhatsAppNotificationService whatsAppService;
    
    public void sendConfirmation(String phone, String clientName, 
                                String service, String date, String time) {
        try {
            List<String> params = Arrays.asList(clientName, service, date, time);
            whatsAppService.sendTemplateByType(phone, "appointment-confirmation", params);
            log.info("WhatsApp sent to {}", phone);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp: {}", e.getMessage());
        }
    }
}
```

### Con idioma específico

```java
// Usa lenguaje por defecto (configurado en YAML)
whatsAppService.sendTemplateByType("34647918823", "appointment-confirmation", params);

```

## 📀 API

Alto nivel (lo que debes usar):

```java
String sendTemplateByType(String phone, String templateType, 
                         String language, List<String> params) throws Exception

String sendTemplate(String phone, String templateName, 
                   String language, List<String> params, List<String> buttons) throws Exception

boolean isConfigured()
```

## 📝 Notas

- Las plantillas deben estar aprobadas en Meta
- Los números se limpian automáticamente (solo dígitos)
- Parámetros deben ser válidos y no vacíos
- Reintentos automáticos en fallos transitorios

## 📄 Licencia

Apache License 2.0
