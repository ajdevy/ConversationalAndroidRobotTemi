# Product Roadmap

1. [ ] **Backend API Infrastructure** — Set up Spring Boot backend with OAuth2 security, PostgreSQL database, and Spring Boot Actuator for monitoring. Create Kotlin multiplatform module for shared POJOs between backend and Android client. `M`

2. [ ] **Restaurant Management System** — Implement restaurant entity with CRUD operations. Create API endpoints for managing restaurant details (name, address, hours, contact info). Each restaurant is isolated in the system. `S`

3. [ ] **User & Permissions Model** — Build user management system with role-based access control (Admin, Manager, Staff). Implement permissions model where users can only access specific restaurants. Admins can assign restaurants to users. `M`

4. [ ] **Menu Management System** — Create menu entities (categories, items, descriptions, prices, ingredients, allergens) per restaurant. Build API endpoints for full CRUD operations on menus. Each menu belongs to exactly one restaurant. `M`

5. [ ] **Basic Temi Android App** — Develop foundational Android app using Temi SDK that runs on the robot, displays UI, and establishes connection with backend API. Include basic navigation and screen layout. `S`

6. [ ] **Visual Status Indicator UI** — Implement visual feedback system on Android app with animated status circle. Display red pulsating circle during thinking/processing, green pulsating circle when speaking/responding, and grey static circle when idle and ready. Use Jetpack Compose animations. `XS`

7. [ ] **Robot-Restaurant Association** — Implement robot registration system where each robot is assigned to exactly one restaurant. Robot can only access menu and information for its assigned restaurant. Include robot authentication with backend. `S`

8. [ ] **Knowledge Base System** — Implement general knowledge base storage for restaurant information (policies, hours, amenities, etc.) in PostgreSQL. Create API endpoints for storing and querying non-menu information. `S`

9. [ ] **Test Data Generation** — Create database seeding scripts with comprehensive test data: multiple restaurants, sample menus with various cuisines, users with different roles, permission assignments, and robot configurations. `S`

10. [ ] **Speech Recognition Integration** — Integrate speech-to-text service optimized for noisy environments into the Temi robot. Test and tune for loud restaurant conditions with background noise handling. Connect to status indicator to show listening state. `M`

11. [ ] **LLM Integration - Local** — Integrate Gemma 2/3 local LLM model into the Android app for offline conversational capabilities. Implement basic prompt engineering for restaurant Q&A scenarios. Update status indicator to red during LLM processing. `L`

12. [ ] **LLM Integration - Cloud API** — Add OpenAI GPT-4o API integration as primary conversational engine with fallback to local model. Implement hybrid switching logic based on connectivity. Ensure status indicator reflects processing state. `S`

13. [ ] **Menu-Aware LLM Prompts** — Enhance LLM prompts to automatically search and reference restaurant menu when answering customer questions. Train prompts to handle menu-specific queries (ingredients, prices, recommendations, allergens). `M`

14. [ ] **Interruption Detection & Handling** — Build real-time interruption detection that stops TTS output when user speaks, clears conversation state, and reevaluates response based on new input. Status indicator should immediately switch from green to red when interrupted. `M`

15. [ ] **Multilingual Support** — Extend speech recognition and LLM responses to support Spanish and Portuguese in addition to English. Test language detection and switching for all features including menu queries. `M`

16. [ ] **Text-to-Speech Output** — Implement natural-sounding TTS in all three supported languages (English, Spanish, Portuguese) with appropriate voice selection for restaurant context. Connect to status indicator to show green during speech output. `S`

17. [ ] **Unit Testing & Maximum Coverage** — Implement comprehensive unit tests for all backend code using JUnit 5 and MockK. Achieve maximum code coverage (target 90%+) for services, repositories, controllers, and business logic. Configure coverage reports in Gradle. `L`

18. [ ] **End-to-End Testing & Tuning** — Conduct comprehensive testing in simulated loud environments. Test multi-restaurant setup, permissions enforcement, menu queries across different restaurants, voice interaction quality, and status indicator behavior. `M`

19. [ ] **Admin Dashboard** — Create web interface for admins and managers to manage restaurants, menus, users, permissions, and robot assignments. Include restaurant-specific views for managers and global views for admins. `L`

20. [ ] **Analytics & Monitoring** — Implement logging and metrics collection for customer interactions, common questions, menu queries, response quality, per-restaurant usage statistics, and system performance through Spring Boot Actuator. `M`

> Notes
> - Order items by technical dependencies and product architecture
> - Each item should represent an end-to-end (frontend + backend) functional and testable feature
> - Multi-restaurant and permissions features are core to the platform and should be implemented early
> - Each robot serves exactly one restaurant and can only access that restaurant's menu
> - Visual status indicator should be integrated early so all conversational features can update it appropriately
