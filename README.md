# **Unikitchen - Installation und Startanleitung**

## **Voraussetzungen**
Bevor du startest, stelle sicher, dass folgende Voraussetzungen erfüllt sind:

1. **Docker und Docker Compose** sind installiert und eingerichtet.
    - Installationsanleitung: [Docker für Ubuntu](https://docs.docker.com/engine/install/ubuntu/)
2. **Node.js** und **npm** sind installiert.
    - Installationsanleitung: [Node.js für Ubuntu](https://nodejs.org/en/download/package-manager/#debian-and-ubuntu-based-linux-distributions)
3. **Java 17** ist installiert und als Standard-Java-Version konfiguriert.
    - Installationsanleitung: [Java 17 für Ubuntu](https://docs.oracle.com/en/java/javase/17/install/overview-jdk-installation.html)

## **Server starten**

### **1. Docker Desktop starten**
Stelle sicher, dass Docker Desktop läuft, bevor du fortfährst.

### **2. Server-Verzeichnis wechseln**
```bash
cd Unikitchen/Server/unikitchen
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

### **1. Abhängigkeiten installieren**
Bevor du das Frontend startest, stelle sicher, dass alle benötigten Abhängigkeiten installiert sind:
```bash
npm install
```

### **2. Frontend-Verzeichnis wechseln**
Wechsle ins Frontend-Verzeichnis:
```bash
cd Unikitchen/Client/unikitchen-frontend
```

### **3. Frontend starten**
Starte das Frontend:
```bash
npm start
```

## **Zusammenfassung der Befehle**

### **Server**
- **Starten**:
  ```bash
  cd Unikitchen/Server/unikitchen
  docker-compose up -d
  ```
- **Stoppen**:
  ```bash
  docker-compose stop
  ```

### **Frontend**
- **Installieren**:
  ```bash
  cd Unikitchen/Client/unikitchen-frontend
  npm install
  ```
- **Starten**:
  ```bash
  npm start
  ```


