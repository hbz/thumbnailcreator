# Thumbnailcreator - Spring Boot API

Dieses Projekt ist eine Spring-Boot-basierte API mit integriertem **Thymeleaf-Frontend**.  
Es erstellt Thubnails, aus Bildern, die von einer URL kommen.

## Thymeleaf – Was ist das & warum wird es genutzt?

## **Was ist Thymeleaf?**
Thymeleaf ist ein **serverseitiges Java-Template-Engine** für HTML.  
Es verarbeitet HTML-Dateien und bindet dynamische Daten aus Spring Boot ein.

## **Was ermöglicht es?**
- dynamisches Rendern von HTML-Formularen  
- Schleifen, Bedingungen, Validierungsfehler direkt im HTML  
- einfache Kommunikation zwischen Spring-Backend und HTML  
- kein separates JavaScript-Frontend notwendig  

In dieser Anwendung werden Thumbnails aus URL-Bilder erstellt.

---

### ⚙️ Voraussetzungen

- Java JDK (hier: JDK21)
- Maven  
- Entwicklungsumgebung (z.B. Eclipse) 
- Lombok (für automatische Getter/Setter/Builder)    

> **Hinweis:** Lombok muss in Eclipse installiert und aktiviert sein, damit die Annotationen korrekt erkannt werden.
---

### Projekt in Eclipse importieren

1. **Eclipse öffnen**  
2. `File` → `Import`  
3. `Maven` → `Existing Maven Projects`  
4. Projektordner auswählen  

Eclipse lädt die Dependencies automatisch.

---

## Build der Anwendung (WAR-Datei erstellen)

- Die WAR-Datei wird mit folgendem Befehl erstellt:  
  ````mvn clean install````

### Ergebnis

- Die erzeugte WAR-Datei befindet sich unter `/target/`

---

## Deployment

- Die erzeugte WAR-Datei (`thumbs.war`) kann nun auf den Server hochgeladen und dort deployed werden.
