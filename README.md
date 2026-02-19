# WhatsApp Notifications Library

Libreria Java para integrar WhatsApp Cloud API en aplicaciones Spring Boot.

## Compilar

```bash
mvn clean install
```

## Usar en otro proyecto

Agregar dependencia en pom.xml:

```xml
<dependency>
    <groupId>com.whatsapp</groupId>
    <artifactId>notifications-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuracion

En application.properties:

```properties
whatsapp.enabled=true
whatsapp.phone-number-id=TU_PHONE_ID
whatsapp.access-token=TU_TOKEN
whatsapp.webhook-token=TU_WEBHOOK_TOKEN
whatsapp.max-retries=3
```

## Uso

```java
@Autowired
private WhatsAppClient whatsAppClient;

// Enviar mensaje de texto
whatsAppClient.sendTextMessage("34123456789", "Hola!");

// Enviar mensaje interactivo
List<InteractiveButton> buttons = Arrays.asList(
    new InteractiveButton("confirm", "Confirmar"),
    new InteractiveButton("cancel", "Cancelar")
);
whatsAppClient.sendInteractiveMessage("34123456789", "Confirmas?", buttons);
```
