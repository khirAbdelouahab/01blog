# 01Blog

01Blog is a full-stack social blogging platform where students document their learning journey, share posts with media, subscribe to peers, and interact through likes, comments, and notifications. Administrators moderate content with dedicated tools.

## Table of Contents
1. [Architecture](#architecture)
2. [Tech Stack](#tech-stack)
3. [Core Features](#core-features)
4. [Getting Started](#getting-started)
   - [Bare Metal Setup](#bare-metal-setup)
   - [Docker Setup](#docker-setup)
5. [Scripts](#scripts)
6. [Project Structure](#project-structure)

## Architecture
- **Backend**: Spring Boot 3 REST API (JWT auth, role-based access, media storage, notifications).
- **Frontend**: Angular 20 SPA with Bootstrap styling.
- **Database**: Mysql (JPA/Hibernate).
- **Media Storage**: File system (`backend/uploads`) exposed to both the app and Docker volume.

## Tech Stack
| Layer      | Technologies                                                                 |
|------------|-------------------------------------------------------------------------------|
| Backend    | Spring Boot 3, Spring Security, Spring Data JPA, JWT (jjwt), PostgreSQL      |
| Frontend   | Angular 20, Angular Router, RxJS, Bootstrap 5, Quill (post editor)           |
| Build/Tools| Maven, npm, Docker, Docker Compose                                           |

## Core Features
- JWT authentication with role-based access (user/admin), hashed passwords, refresh tokens.
- User profiles ("blocks") with avatar uploads, bio, post history, subscriptions and activity feed.
- Posts CRUD with rich-text content, media uploads, likes, comments, infinite scroll and feeds.
- Notifications for subscribers when posts are published; mark as read/unread.
- Reporting system for posts/comments/users, admin moderation dashboard (users, posts, reports).
- Media storage with validation and cleanup helpers.

## Getting Started

### Prerequisites
- **Bare metal**: Java 17, Maven 3.9+, Node.js 20+, npm, Mysql (local or remote), Angular CLI (`npm install -g @angular/cli`).
- **Docker**: Docker Engine 24+, Docker Compose.

### Bare Metal Setup
1. **Database**
   - Create a Mysql database (e.g., `db01blog`) and user with privileges.
   - Update `backend/src/main/resources/application.properties` with your DB host/credentials.

2. **Backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   - Default port: `8080`

3. **Frontend**
   ```bash
   cd frontend
   npx ng s
   ```
   - Angular dev server listens on `http://localhost:4200`
3. **Both Servers**
   ```bash
   ./start-apps.sh
4. **Media uploads**
   - Ensure `backend/uploads` exists (created automatically) and accessible for storing avatar/post media.

### Docker Setup
1. Build and run all services:
   ```bash
   docker compose up --build
   ```
2. Services:
   - `db01blog`: MySQl (port `3307`)
   - `backend`: Spring Boot (`http://localhost:8080`)
   - `frontend`: Angular dev server (`http://localhost:4200`)
3. Volumes:
   - `mysql_data:/var/lib/mysql`: persistent Mysql data

## Project Structure
```
01blog/
├── backend/                 # Spring Boot API
│   ├── Dockerfile
│   ├── src/main/java/...    # Packages: auth, post, profile, notification, media
│   └── src/main/resources/application.properties
├── frontend/                # Angular app
│   ├── src/app/             # Core, features (feed, posts, profile, admin), shared components
│   └── ...
├── docker-compose.yml
├── start-apps.sh
└── README.md

```