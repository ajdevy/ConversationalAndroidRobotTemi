# Spec Requirements: Basic Temi Android App

## Initial Description
Develop foundational Android app using Temi SDK that runs on the robot, displays UI, and establishes connection with backend API. Include basic navigation and screen layout.

## Requirements Discussion

### First Round Questions

**Q1: Development Environment & SDK Setup**
What development environment setup is needed? Should this support emulator testing or only physical Temi robot deployment? Are there specific Temi SDK version requirements or should we reference the official Maven repository for the latest compatible version?

**Answer:** Should support emulator, reference the official Maven

**Q2: Backend API Connection Configuration**
How should the backend API endpoint be configured? Should it be hardcoded for this initial version, configurable via build variants (dev/staging/prod), or user-configurable through settings?

**Answer:** Hardcode

**Q3: Robot Registration & Restaurant Assignment**
What's the expected flow for robot registration and restaurant assignment? Should the app automatically register on first launch, require manual input of restaurant ID, or use a QR code scan for configuration?

**Answer:** Should be one-time login, support QR based login. The QR code should have an OAuth token to be used for the robot. The token should have an expiration date and should be possible to refresh a token with a refresh token, which has a longer lifetime period.

**Q4: Initial UI Screens & Navigation**
What screens are needed for the MVP? Should we include: splash screen, main dashboard, settings screen, or just a basic connection status view? What navigation pattern is preferred (bottom nav, drawer, or single screen)?

**Answer:** Also add a settings screen, which is shown when you click 6 times on the indicator. It should have a field to configure the backend URL and to toggle local/remote LLM.

**Q5: Shared Kotlin Multiplatform POJOs**
Should POJOs/data models be shared between this app and backend systems? If yes, should they live in this repository or a separate shared library? What serialization library preference (Gson, Moshi, kotlinx.serialization)?

**Answer:** In a different repository. The app should have POJOs that it only needs to get the data from the backend.

**Q6: Dependency Injection & Architecture Setup**
What level of architectural setup is needed? Should we scaffold full DI (Hilt/Koin), repository pattern, and MVVM layers, or keep it minimal for this foundation? Any preference for package structure (feature-first vs layer-first)?

**Answer:** Setup all layers, use feature-first package layout

**Q7: Temi SDK Integration Scope**
Which Temi SDK capabilities should be integrated in this foundation? Navigation APIs, face recognition, voice interaction, or just basic robot status monitoring? Should we create abstraction layers over Temi SDK calls?

**Answer:** Add all possible robot statuses to the settings screen. For now no other feature other than talking are needed.

**Q8: Feature Exclusions**
Are there any specific features we should explicitly NOT include in this foundation to avoid scope creep? (e.g., no order management, no user authentication beyond robot registration, no offline mode)

**Answer:** (No specific exclusions mentioned)

### Existing Code to Reference
No similar existing features identified for reference.

### Follow-up Questions
No follow-up questions needed.

## Visual Assets
No visual files found in the planning/visuals directory.

## Requirements Summary

### Functional Requirements

#### Authentication & Security
- QR code-based OAuth token authentication for robot login
- One-time login flow with persistent session
- OAuth token with expiration date
- Refresh token mechanism with longer lifetime period for token renewal
- Token management and automatic refresh handling

#### UI Screens & Navigation
- Main application screens with basic navigation
- Hidden settings screen accessible by clicking 6 times on an indicator
- Settings screen features:
  - Backend URL configuration field (overrides hardcoded default)
  - Toggle switch for local/remote LLM selection
  - Display of all possible Temi robot statuses
- Splash/initial screen for QR code scanning during first launch

#### Backend Integration
- Hardcoded backend API endpoint as default
- User-configurable backend URL override via settings
- REST API communication with backend
- POJO models for receiving data from backend
- Serialization/deserialization of API responses

#### Temi SDK Integration
- Voice interaction/talking capabilities
- Robot status monitoring for all available states
- Display of comprehensive robot status information in settings
- Emulator support for development and testing

#### Development Environment
- Android application targeting Temi robot hardware
- Emulator compatibility for testing without physical device
- Reference to official Temi SDK Maven repository
- Use of latest compatible Temi SDK version from Maven

### Architecture & Technical Setup

#### Dependency Injection & Layers
- Full dependency injection setup (Hilt or Koin)
- Complete architectural layers:
  - Presentation layer (UI/ViewModels)
  - Domain layer (Use Cases/Business Logic)
  - Data layer (Repositories/Data Sources)
- MVVM architecture pattern
- Feature-first package structure organization

#### Data Management
- Separate repository for shared Kotlin Multiplatform POJOs
- Local POJOs in app for backend API data models only
- Token storage for OAuth and refresh tokens
- Persistent session management

### Reusability Opportunities
- OAuth token management module can be reused across robot applications
- QR code authentication flow pattern reusable for other robot deployments
- Temi SDK abstraction layer for future Temi-based applications
- Backend API client foundation for expanded features
- Feature-first architecture provides clean separation for module extraction

### Scope Boundaries

**In Scope:**
- Basic Android app structure with Temi SDK integration
- QR code-based OAuth authentication with token refresh
- Hardcoded backend URL with user override capability in settings
- Hidden settings screen (6-tap access) with:
  - Backend URL configuration
  - Local/remote LLM toggle
  - Complete robot status display
- Voice/talking capability via Temi SDK
- Full architectural setup (DI, MVVM, repository pattern)
- Feature-first package layout with all layers
- Emulator support for development
- POJOs for backend data consumption
- Maven-based Temi SDK integration

**Out of Scope:**
- Order management features
- Customer-facing UI and interactions
- Advanced Temi navigation capabilities
- Face recognition features
- Complex voice command processing beyond basic talking
- Offline mode functionality
- Multi-robot management
- Analytics and logging infrastructure
- Admin portal or web dashboard
- Restaurant-specific customizations
- Menu management
- Integration with POS systems

### Technical Considerations

#### Integration Points
- Temi SDK via official Maven repository
- Separate Kotlin Multiplatform POJOs repository (dependency)
- Backend API (REST endpoints for robot data)
- OAuth authentication service for token generation/refresh
- QR code scanning library integration

#### Technical Constraints
- Must run on Temi robot hardware (Android-based)
- Must support Android emulator for development
- OAuth token lifecycle management required
- Feature-first package structure mandates clear module boundaries
- Dependency injection framework must support Android/Temi environment

#### Technology Preferences
- Kotlin as primary development language
- Kotlin Multiplatform for shared data models (external repository)
- Dependency injection framework: Hilt or Koin
- MVVM architecture pattern
- Repository pattern for data layer
- Feature-first over layer-first package organization
- Maven for Temi SDK dependency management
- QR code scanning library (e.g., ZXing or ML Kit)

#### Security Considerations
- Secure storage of OAuth and refresh tokens
- Token expiration monitoring and automatic refresh
- HTTPS for backend API communication
- QR code validation and token verification
- Settings screen access protection (6-tap hidden mechanism)
