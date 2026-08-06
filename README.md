# Cloud-Based Real-Time Chat Application

A scalable real-time chat application built with **Java 21**, **Spring Boot**, **PostgreSQL**, and **WebSockets**. The project is designed using a modular backend architecture with a focus on performance, maintainability, and real-time communication.

---

## Overview

This project demonstrates how to build a production-style chat application that supports secure authentication, persistent messaging, and low-latency real-time communication.

The primary objective is to learn modern backend engineering concepts including REST APIs, WebSocket communication, database design, authentication, caching, and scalable software architecture.

---

## Features

### User Management
- User registration
- Secure login
- JWT Authentication
- Password encryption using BCrypt
- User profile management

### Real-Time Messaging
- Instant messaging using WebSockets
- One-to-one private chats
- Online/offline status
- Message delivery
- Read receipts (planned)
- Typing indicator (planned)

### Database
- PostgreSQL integration
- Persistent message storage
- User information storage
- Conversation history

### Backend
- RESTful API
- Spring Boot
- Spring Security
- Spring Data JPA
- Validation
- Exception handling
- DTO architecture

### Performance
- Redis caching (planned)
- Connection pooling
- Optimized database queries
- Pagination support

### Future Features
- Group chats
- Voice messages
- File sharing
- Image sharing
- Push notifications
- Video calls
- AI chat assistant
- Message search
- End-to-end encryption
- Multi-device synchronization

---

# Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- WebSocket (STOMP)

## Database

- PostgreSQL

## Authentication

- JWT
- BCrypt Password Encoder

## Future Technologies

- Redis
- Docker
- Kubernetes
- RabbitMQ / Kafka
- Nginx
- Prometheus
- Grafana

---

# Project Structure

```
src
├── main
│   ├── java
│   │   └── com.chatapp
│   │       ├── config
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entity
│   │       ├── dto
│   │       ├── security
│   │       ├── websocket
│   │       ├── exception
│   │       └── util
│   └── resources
│       ├── application.properties
│       └── static
```

---

# Architecture

```
Client
   │
   ▼
REST API / WebSocket
   │
   ▼
Spring Boot Application
   │
   ├── Controllers
   ├── Services
   ├── Security
   ├── WebSocket
   ├── Repository
   │
   ▼
PostgreSQL Database
```

---

# Database

## User

| Field | Type |
|--------|------|
| id | Long |
| username | String |
| email | String |
| password | String |
| createdAt | Timestamp |

---

## Message

| Field | Type |
|--------|------|
| id | Long |
| senderId | Long |
| receiverId | Long |
| message | String |
| sentAt | Timestamp |
| status | Delivered / Read |

---

# API Endpoints

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | /api/auth/register |
| POST | /api/auth/login |

---

## User

| Method | Endpoint |
|---------|----------|
| GET | /api/users |
| GET | /api/users/{id} |
| PUT | /api/users/{id} |

---

## Chat

| Method | Endpoint |
|---------|----------|
| GET | /api/messages |
| POST | /api/messages |

---

# WebSocket

```
/ws/chat
```

Example topics

```
/topic/messages
/user/queue/messages
```

---

# Installation

## Clone Repository

```bash
git clone https://github.com/yourusername/chat-application.git
```

Go into project

```bash
cd chat-application
```

---

## Configure PostgreSQL

Create a database

```
chat_db
```

Update

```
src/main/resources/application.properties
```

Example

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Run

```bash
mvn clean install

mvn spring-boot:run
```

or

Run the `Application.java` class from IntelliJ IDEA.

---

# Screenshots

Coming Soon

- Login Page
- Chat Dashboard
- Private Chat
- User Profile

---

# Roadmap

- [x] Spring Boot setup
- [x] PostgreSQL integration
- [x] User entity
- [x] Message entity
- [ ] JWT Authentication
- [ ] WebSocket messaging
- [ ] Redis integration
- [ ] Docker support
- [ ] Kubernetes deployment
- [ ] CI/CD Pipeline
- [ ] Monitoring
- [ ] AI-powered assistant

---

# Learning Goals

This project is built to strengthen knowledge in:

- Backend Development
- REST APIs
- Spring Boot
- Software Architecture
- Authentication
- Database Design
- Real-Time Communication
- Scalable System Design
- Cloud Deployment
- DevOps Fundamentals

---

# Contributing

Contributions, suggestions, and feedback are welcome.

1. Fork the repository
2. Create a new branch
3. Commit your changes
4. Open a Pull Request

---

# License

This project is licensed under the MIT License.

---

# Author

**Shadil**

B.Tech Computer Science Engineering (AI & DS)

Backend & AI Engineering Enthusiast

Building scalable backend systems and AI-powered applications.
