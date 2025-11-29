# Tech Stack

This document defines the technical stack for the Temi Restaurant Assistant PoC. All development should adhere to these technology choices to maintain consistency.

## Robot Platform
- **Hardware Platform:** Temi Robot (robotemi.com)
- **Robot SDK:** Temi SDK (https://github.com/robotemi/sdk)
- **Robot OS:** Android

## Frontend (Android App)
- **Application Framework:** Android Native
- **Language/Runtime:** Kotlin
- **Build Tool:** Gradle
- **UI Framework:** Jetpack Compose
- **Architecture:** Clean Architecture with MVVM pattern
- **Concurrency:** Kotlin Coroutines
- **Reactive Streams:** Kotlin Flow
- **Local Database:** Room
- **Dependency Injection:** Koin
- **HTTP Client:** Retrofit + OkHttp
- **JSON Serialization:** Moshi (Retrofit converter)
- **Temi Integration:** Temi SDK for robot-specific features (navigation, face recognition, etc.)

## Backend
- **Application Framework:** Spring Boot
- **Language/Runtime:** Kotlin with Multiplatform support
- **Package Manager:** Gradle
- **Security:** OAuth 2.0 for authentication and authorization
- **Monitoring:** Spring Boot Actuator

## Shared Code
- **Multiplatform Module:** Kotlin Multiplatform (KMP)
- **Shared Components:** POJOs, data models, business logic between Android client and Spring Boot backend

## Database & Storage
- **Backend Database:** PostgreSQL
- **Backend ORM:** Spring Data JPA / Hibernate
- **Schema Management:** Flyway or Liquibase (for database migrations)
- **Android Local Database:** Room (SQLite wrapper)
- **Android Data Caching:** Room with Flow for reactive updates

## AI & Machine Learning
- **Local LLM:** Gemma 2/3 (Google) - ~5GB model for offline capability
- **Cloud LLM:** OpenAI GPT-4o API (primary, best quality)
- **LLM Orchestration:** Hybrid fallback: Cloud API → Local model when offline
- **Speech-to-Text:** TBD - Service optimized for noisy environments (e.g., Google Cloud Speech-to-Text, Azure Speech, or Whisper)
- **Text-to-Speech:** TBD - Multilingual TTS service (Google TTS, Amazon Polly, or similar)
- **Languages Supported:** English, Spanish, Portuguese

## Testing & Quality
- **Backend Testing:** JUnit 5, MockK, Spring Boot Test
- **Android Testing:**
  - **Unit Tests:** JUnit 4, MockK
  - **UI Tests:** Kaspresso, Compose UI Testing
  - **Coroutine Testing:** kotlinx-coroutines-test
  - **Flow Testing:** Turbine
  - **Room Testing:** Room testing library with in-memory database
  - **Network Testing:** MockWebServer (OkHttp) for Retrofit API testing
- **Code Coverage:** JaCoCo (Java Code Coverage) for backend - target 90%+ coverage
- **Coverage Reports:** Gradle JaCoCo plugin for HTML/XML reports
- **Code Quality:** Detekt (Kotlin linting), ktlint (formatting)
- **API Testing:** Postman / REST Assured
- **Test Data:** Database seeding scripts with comprehensive test datasets

## Deployment & Infrastructure
- **Backend Hosting:** local server
- **Database Hosting:** self-hosted
- **CI/CD:**  GitLab CI
- **Containerization:** Docker (optional, for backend deployment)

## Third-Party Services
- **Authentication:** OAuth 2.0 (Spring Security OAuth)
- **LLM API:** OpenAI API (GPT-4o)
- **Speech Services:** TBD based on testing in noisy environments
- **Monitoring:** Spring Boot Actuator + Prometheus/Grafana (optional)
- **Logging:** SLF4J + Logback

## Development Tools
- **IDE:** IntelliJ IDEA (backend) / Android Studio (Android app)
- **Version Control:** Git
- **API Documentation:** Swagger/OpenAPI (SpringDoc)
- **Postman:** API testing and documentation

## Architecture Patterns
- **Backend:** RESTful API architecture
- **Android:** Clean Architecture with MVVM pattern
  - **Presentation Layer:** Jetpack Compose UI + ViewModels
  - **Domain Layer:** Use Cases / Interactors
  - **Data Layer:** Repositories, Room DAOs, Remote Data Sources (Retrofit API services)
  - **Dependency Management:** Koin for dependency injection
  - **Async Operations:** Kotlin Coroutines + Flow
  - **Network Layer:** Retrofit with OkHttp interceptors for auth, logging
- **Code Sharing:** Kotlin Multiplatform for shared data models
- **Security:** Token-based authentication with OAuth 2.0
- **Multi-Tenancy:** Restaurant-level data isolation with permissions model
- **Authorization:** Role-based access control (RBAC) - Admin, Manager, Staff roles
- **Data Model:** One-to-Many relationships (Restaurant → Menus, Users → Restaurants, Restaurant → Robots)
