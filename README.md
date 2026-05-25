# Rekapin - Backend API

Rekapin is a simple sales recording backend application designed specifically for students and small MSMEs (UMKMs) selling offline. This application aims to simplify transaction recording, product management, and automatic profit monitoring without the complexity of traditional Point of Sale (POS) systems.

## Key Features

- **Security Authentication**: Uses JWT (JSON Web Token) for a secure and stateless login system.
- **Product Management**: CRUD (Create, Read, Update, Delete) products with optional stock management features.
- **Transaction Recording**: Simple transaction flow that automatically calculates subtotal, total sales, and estimated profit based on price snapshots at the time of transaction.
- **Dashboard Summary**: Real-time information on today's total sales, today's profit, transaction count, and best-selling products.
- **History Reports**: Access transaction history with date range filters.
- **Account & Data Management**: Features to delete transaction history and permanently delete user accounts.

## Technologies Used

- **Programming Language**: Java 25 (Using the latest features like Java Records).
- **Framework**: Spring Boot 4.0.6 (Spring Framework 7.0).
- **Database**: MySQL.
- **Security**: Spring Security & JWT (jjwt 0.12.6).
- **API Documentation**: SpringDoc OpenAPI / Swagger UI.
- **Others**: 
  - Lombok (Boilerplate reduction)
  - JSpecify (Standard Null-safety)
  - JPA / Hibernate (Database mapping)

## Prerequisites

Before running the application, make sure you have installed:
- Java Development Kit (JDK) 25
- MySQL Server
- Gradle (Optional, already available via `gradlew`)

## Installation & Configuration

1. **Clone Repository**
   ```bash
   git clone https://github.com/username/rekapin-be.git
   cd rekapin-be
   ```

2. **Database Configuration**
   Create a new database in MySQL named `rekapin_db`. Then open the `src/main/resources/application.properties` file and adjust your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/rekapin_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   
   rekapin.jwt.secret=insert_secret_key_at_least_32_characters_here
   rekapin.jwt.expiration=86400000
   ```

3. **Run the Application**
   Use the provided Gradle wrapper:
   ```bash
   ./gradlew bootRun
   ```

## API Documentation

This application is equipped with Swagger UI for interactive API exploration:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Standard API Response Structure
All API responses use a consistent format:
```json
{
  "success": true,
  "message": "Success or error message",
  "data": { ... }
}
```

## Testing Guide

### Using Postman
1. Open Postman, click **Import**.
2. Enter the URL `http://localhost:8080/v3/api-docs`.
3. Once imported, login via `/api/auth/login` to get a token.
4. Set **Authorization** at the Collection level to **Bearer Token** and paste the token.

## Project Structure

```text
rekapin-be
├── src/main/java/com/ut/rekapinbe
│   ├── controller/   # API Handlers (REST Controllers)
│   ├── dto/          # Data Transfer Objects (Java Records)
│   ├── entity/       # Database Models (JPA Entities)
│   ├── exception/    # Global Exception Handling
│   ├── repository/   # Database Queries (Spring Data JPA)
│   ├── security/     # Security & JWT Configuration
│   └── service/      # Main Business Logic
└── src/main/resources
    └── application.properties # Application Configuration
```
