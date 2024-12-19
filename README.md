# **Unikitchen - Installation und Startanleitung**

## **Voraussetzungen**
Bevor du startest, stelle sicher, dass folgende Voraussetzungen erfüllt sind:

1. **Docker und Docker Compose** sind installiert und eingerichtet.
    - Installationsanleitung: [Docker für Ubuntu](https://docs.docker.com/engine/install/ubuntu/)
2. **Node.js** (v16 oder höher) und **npm** sind installiert.
    - Installationsanleitung: [Node.js für Ubuntu](https://nodejs.org/en/download/package-manager/#debian-and-ubuntu-based-linux-distributions)

## **Server starten**

### **1. Docker Desktop starten**
Stelle sicher, dass Docker Desktop läuft, bevor du fortfährst.

### **2. Server-Verzeichnis wechseln**
```bash
cd dreamteam-se2-hausarbeit/Server/unikitchen
```

### **3. Docker-Container starten**
Starte den Server im Hintergrund mit Docker Compose:
```bash
docker-compose up -d
```

### **4. Server stoppen**
Um den Server zu stoppen, führe folgenden Befehl aus:
```bash
docker-compose stop
```

## **Frontend starten**

### **1. Frontend-Verzeichnis wechseln**
Wechsle ins Frontend-Verzeichnis:
```bash
cd dreamteam-se2-hausarbeit/Client/unikitchen-frontend
```

### **2. Frontend starten**
Starte das Frontend:
```bash
npm start
```

## **Zusammenfassung der Befehle**

### **Server**
- **Starten**:
  ```bash
  cd dreamteam-se2-hausarbeit/Server/unikitchen
  docker-compose up -d
  ```
- **Stoppen**:
  ```bash
  docker-compose stop
  ```

### **Frontend**
- **Starten**:
  ```bash
  cd dreamteam-se2-hausarbeit/Client/unikitchen-frontend
  npm start
  ```


## **Projektbeiträge der Teammitglieder**

### **Markus Artemov**
- Aufbau der Grundarchitektur (Spring Boot, Docker Compose, MariaDB).
- Implementierung von Authentifizierung (JWT) und grundlegenden CRUD-Endpunkten für Rezepte.
- Auslagerung der Datenlogik ins Frontend-Service-Modul.
- Einfache Tests für Authentifizierung und CRUD-Funktionen.

### **Mohammad Nour Masri**
- Entwicklung erweiterter Features: Bewertungen, Favoriten, Bild-Uploads.
- Überarbeitung des Exception Handlings für konsistente Fehlermeldungen.
- Behebung kleinerer Bugs und Optimierung der Backend-Logik.
- Entwicklung der REST-API-Endpunkte für Bewertungen und Favoriten.

### **Justus Schmiernow**
- Gestaltung und Entwicklung der UI mit Vue.js.
- Einbindung der Authentifizierung und Login-Status-basierte UI-Anpassungen.
- Anpassung von Backend-Datenstrukturen für optimale Frontend-Integration.
- Manuelle Tests der Frontend-Funktionen.