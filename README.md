# 🧭 TripWise API

The **TripWise Backend** is a **Spring Boot application** that handles all server-side logic and database operations for the TripWise platform.  
Users can create trips, manage budgets, and add activities to their journeys.

---

## 🚀 Features

- **Trip management** – create, update, and delete trips  
- **Budget tracking** – store, update, and delete budgets per trip  
- **Activity management** – link activities to specific trips  
- **PostgreSQL integration** – relational database for persistent data  
- **Docker support** – easy to run in a containerized environment  

---

## 🧱 Tech Stack

| Component | Technology |
|------------|-------------|
| Backend framework | Spring Boot (Java 17+) |
| Database | PostgreSQL |
| Build tool | Gradle |
| Containerization | Docker |
| ORM | Spring Data JPA / Hibernate |
| REST API | Spring Web |

---

## ⚙️ Local Setup (Windows)

### 1. Prerequisites
- **Java 17+** (e.g., [AdoptOpenJDK](https://adoptium.net/))
- **Gradle** (included via `./gradlew`)
- **Docker Desktop** (for containerization)
- **Git**

### 2. Clone the repository

```powershell
git clone https://github.com/your-username/tripwise-backend.git
cd "TripWise API"
```

### 3. Configure Environment Variables

You can set environment variables in Windows using PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/tripwise"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="yourpassword"
```

Or edit `src/main/resources/application.properties` as needed.

### 4. Start PostgreSQL with Docker Compose

A sample `docker-compose.yml` is provided. Start the database with:

```powershell
docker-compose up -d
```

This will launch a PostgreSQL instance accessible at `localhost:5432`.

### 5. Create the Database (if not auto-created)

If the database is not created automatically, connect and run:

```powershell
docker exec -it tripwise-db psql -U postgres -c "CREATE DATABASE tripwise;"
```

### 6. Run the Application

Build and run with Gradle:

```powershell
./gradlew bootRun
```

Or on Windows:

```powershell
./gradlew.bat bootRun
```

The API will be available at: [http://localhost:8080](http://localhost:8080)

### 7. Run with Docker

Build the image:

```powershell
docker build -t tripwise-api .
```

Run the container:

```powershell
docker run -p 8081:8080 --name tripwise-container --env SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/tripwise --env SPRING_DATASOURCE_USERNAME=postgres --env SPRING_DATASOURCE_PASSWORD=yourpassword tripwise-api
```

---

## 🛠️ Troubleshooting
- Ensure Docker Desktop is running.
- If ports are in use, stop conflicting services or change the port mapping.
- For database connection issues, check credentials and network settings.

## 🧪 Testing
Run tests with:



---
