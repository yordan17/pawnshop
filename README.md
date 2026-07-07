# Pawnshop Management System

A web-based pawnshop management application built with Spring Boot and Thymeleaf.

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.16**
- **Spring MVC** — web layer with Thymeleaf templates
- **Spring Data JPA / Hibernate** — database access
- **Spring Security** — authentication and role-based access control
- **MySQL 8** — relational database
- **Thymeleaf** — server-side HTML templating
- **Bootstrap 5** — UI styling
- **Maven** — build tool

## Features

### Authentication & Authorization
- User registration and login (session-based)
- Two roles: **EMPLOYEE** and **ADMIN**
- Guests can only access the login and registration pages
- Admins have access to edit, delete, and role management operations

### Customers
- Register new customers with personal information
- View customer list and details
- Edit and delete customers (Admin only)

### Pawn Items
- Register items brought in for pawning (name, category, condition, estimated value, interest rate)
- Track item status: `AVAILABLE`, `PAWNED`, `REDEEMED`, `EXPIRED`, `SOLD`
- Manual status transitions with validation
- Edit and delete items (Admin only)

### Contracts
- Create pawn contracts linked to a customer and an item
- Track contract status: `ACTIVE`, `REDEEMED`, `EXPIRED`, `SOLD`
- Real-time accrued interest calculation based on elapsed days
- View all contracts sorted by status (active first) and start date
- Delete contracts (Admin only) — returns item to AVAILABLE

### Payments
- Register three types of payments:
    - **REDEMPTION** — full repayment; contract marked REDEEMED, item returned to AVAILABLE
    - **INTEREST** — pay accrued interest only; due date extended by 1 month, interest resets
    - **PARTIAL** — pay interest + part of principal; loan reduced, due date extended by 1 month
- Auto-calculated payment amount based on selected contract and payment date
- Payment history per contract

### Dashboard
- Summary statistics: total customers, active contracts, pawned items, total payments

## Running the Application

### Prerequisites
- Java 17+
- MySQL 8 running on `localhost:3306`
- Maven

### Setup

1. Clone the repository
2. Create a MySQL database named `pawnshop` (created automatically on first run)
3. Update database credentials in `src/main/resources/application.properties` if needed
4. Run the application:
```bash
mvn spring-boot:run
```
5. Open `http://localhost:8080`

### First-time Setup
Register a user account — it will be created with the **EMPLOYEE** role. To create an **ADMIN** user, update the role directly in the database:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
```

## Integrations

This is a standalone Spring Boot application with no external service integrations.
Database connectivity is handled via Spring Data JPA with a MySQL 8 database.

## Domain Entities

| Entity | Description |
|---|---|
| `Customer` | Person who brings items to the pawnshop |
| `PawnItem` | Item accepted as collateral |
| `Contract` | Pawn agreement between customer and pawnshop |
| `Payment` | Payment made against a contract |
| `User` | System user (employee or admin) |
