# Internal-Business-Automation-Solution

# Iron Lady - Internal Business Automation System

## Overview

Iron Lady Operations System is a web-based internal business automation tool designed to streamline participant and cohort management.

This system replaces manual spreadsheets and scattered tracking with a centralized admin dashboard built using:

- Spring Boot (Backend)
- MySQL (Database)
- HTML, CSS, Bootstrap (Frontend)
- REST APIs for communication

The application focuses on improving operational efficiency in participant management and attendance tracking.

---

## Business Problem

Manual participant tracking through spreadsheets leads to:

- Data inconsistency
- No real-time visibility
- Poor attendance monitoring
- Manual status calculation
- Lack of centralized reporting

This system solves these problems by:

- Providing a centralized dashboard
- Automating CRUD operations
- Tracking participant status
- Managing attendance records
- Displaying live operational metrics

---

## 🎥 Live Demo

A complete execution walkthrough of the Internal Business Automation System can be viewed below:

<img width="1912" height="829" alt="Internal Business Architecture" src="https://github.com/user-attachments/assets/5c9f11e9-cb83-49b0-9dd7-184e3de2906a" />


👉 **Project Demo Video:**  
https://www.loom.com/share/dda5a1c0aa79434fb9a11c3e3461a76f

The demonstration includes:

- Architecture overview (Frontend → Backend → Database)
- Participant Management (Full CRUD)
- Attendance Management
- Real-time Dashboard Analytics
- Business workflow automation explanation


## Features

### 1. Dashboard
- Real-time participant statistics
- Total participants count
- Active, Completed, Dropped breakdown
- Quick access navigation

### 2. Participant Management (Full CRUD)
- Add new participants
- View all participants
- Edit participant details
- Delete participants
- Status tracking (Active / Completed / Dropped)

### 3. Attendance Management
- Date-based attendance tracking
- Mark participants present
- Store attendance records in database

---

## System Architecture

Frontend (HTML + CSS + JS)
        ↓
REST API (Spring Boot)
        ↓
MySQL Database

Three-tier architecture:
- Presentation Layer
- Business Logic Layer
- Data Access Layer

---

## Tech Stack

Backend:
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Hibernate

Database:
- MySQL

Frontend:
- HTML5
- CSS3
- Bootstrap 5
- Vanilla JavaScript

Build Tool:
- Maven

---

## Database Design

### Participants Table

| Field | Type |
|-------|------|
| id | BIGINT (PK) |
| name | VARCHAR |
| email | VARCHAR |
| phone | VARCHAR |
| program_name | VARCHAR |
| cohort_number | INT |
| payment_status | VARCHAR |
| status | VARCHAR |
| notes | TEXT |

### Attendance Table

| Field | Type |
|-------|------|
| id | BIGINT (PK) |
| participant_id | BIGINT (FK) |
| session_date | DATE |
| present | BOOLEAN |

---

## How to Run the Project

### 1. Clone Repository

```bash
git clone <your-repo-url>

2. Configure Database

Create MySQL database:

CREATE DATABASE ironlady_db;


Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/ironlady_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

3. Run Backend

Navigate to backend folder:

cd backend/ops
mvn spring-boot:run


Backend runs at:

http://localhost:8080

4. Run Frontend

Open frontend HTML files using Live Server or browser.

API Endpoints

Participants:

GET /api/participants

POST /api/participants

PUT /api/participants/{id}

DELETE /api/participants/{id}

Attendance:

POST /api/attendance

GET /api/attendance/{date}

Key Learnings

Implemented complete CRUD using REST APIs

Designed relational database with foreign keys

Built layered architecture (Controller, Service, Repository)

Connected frontend with backend via fetch API

Implemented business logic-driven UI

Future Improvements

Authentication and role management

Export reports (PDF/Excel)

Pagination & search

Advanced analytics dashboard

Email notifications

