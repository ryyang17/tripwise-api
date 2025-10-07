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

## ⚙️ Local Setup

### 1. Clone the repository

git clone https://github.com/your-username/tripwise-backend.git
cd tripwise-backend

### 2. Start PostgreSQL locally

Create a new database:
psql -U postgres
CREATE DATABASE tripwise;

Update your application.properties if needed:
spring.datasource.url=jdbc:postgresql://localhost:5432/tripwise
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

### 3. Run the application

./gradlew bootRun

The API will be available at:
http://localhost:8080

### 4. Run with Docker
Build the image
docker build -t tripwise-api .

Run the containerized
docker run -p 8080:8080 --name tripwise-container tripwise-api
