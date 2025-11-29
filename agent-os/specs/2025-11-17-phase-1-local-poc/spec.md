# Specification: Phase 1 - Local Conversational Robot PoC

## Goal
Build a fully functional on-device conversational robot for Temi using local LLM (Gemma 3n-E2B), STT, and TTS to answer restaurant menu and information questions with no cloud dependencies.

## User Stories
- As a restaurant customer, I want to ask the robot questions about the menu so that I can learn about dishes, prices, and ingredients without waiting for staff
- As a restaurant customer, I want to activate the robot by saying "Hey, Temi" or tapping a button so that I can start a conversation naturally
- As a restaurant manager, I want to configure the robot's behavior (wake word sensitivity, speech rate, interruption handling) via a settings screen so that I can optimize it for my restaurant environment

## Specific Requirements

**Wake Word Detection and Manual Activation**
- Implement continuous background listening for "Hey, Temi" wake word when robot is idle (can be toggled on/off in settings)
- Add large manual activation button in settings screen as alternative trigger for conversation
- Wake word detection latency must be under 100ms from speech to activation
- Configure wake word sensitivity via settings screen slider to minimize false positives in noisy restaurant environments
- Background listening must be CPU-efficient to avoid battery drain
- Use Porcupine wake word engine or similar Android-compatible library (TBD during implementation)
- Respect wake word toggle setting (disable background listening when turned off)

**Local Speech-to-Text Processing**
- Use Google on-device Speech Recognition API (android.speech.SpeechRecognizer)
- Transcribe user utterances with target latency under 2 seconds for typical questions
- Implement timeout handling (5 seconds silence) when user doesn't speak after activation
- Handle unclear or garbled speech gracefully with retry prompt
- Support English language only

**Local LLM Integration**
- Use Gemma 3n-E2B model (2GB RAM footprint, mobile-optimized) via Google AI Edge SDK
- Load model at app startup and keep in memory for fast inference
- Target inference latency under 3 seconds for typical menu questions
- Feed TOON-formatted menu and knowledge base data into LLM context for prompt construction
- Implement timeout handling (10 seconds max) with fallback to "I'm not sure" response
- Generate contextually appropriate responses to menu questions (ingredients, prices, descriptions, allergens)
- Handle out-of-scope questions gracefully with polite deflection

**Local Text-to-Speech Output**
- Use Android TextToSpeech API with English language pack
- Configure natural speaking pace with user-adjustable rate via settings screen (50-250 WPM, default 150)
- Implement fallback to display text response if TTS engine fails
- Ensure TTS doesn't conflict with Temi SDK audio features
- Respect TTS speech rate setting from settings screen

**Real-Time Interruption Detection and Handling**
- Monitor audio input continuously while TTS is actively speaking (when interruption detection is enabled in settings)
- Detect when user begins speaking during robot's response (audio level threshold-based or VAD)
- Stop TTS output immediately (target latency under 200ms from user speech to TTS stop)
- Transition status indicator from green (speaking) to red (listening/thinking)
- Clear conversation state and reevaluate response incorporating interruption context
- Ensure audio pipeline doesn't conflict between wake word detection, STT, interruption monitoring, and TTS output
- Respect interruption detection toggle from settings screen (disable monitoring when turned off)

**Fullscreen Pulsating Circle Status Indicator**
- Implement with Jetpack Compose custom animations
- Three distinct visual states: Idle (grey static circle), Listening/Thinking (red pulsating breathing animation), Speaking (green pulsating breathing animation)
- Fullscreen display with smooth state transitions (animated color and pulse changes)
- Pulsating animation: gentle breathing effect (scale 1.0 to 1.15, 1.5 second cycle)
- Circle size adjustable via settings screen (40-80% of screen, default 60%)
- Ensure status indicator updates synchronously with conversation state changes
- When debug info is enabled in settings, overlay transcription text and LLM response on screen

**TOON Format Menu and Knowledge Base Data**
- Store menu data in assets/menu.toon file (dishes, prices, ingredients, allergens, descriptions)
- Store restaurant information in assets/knowledge-base.toon file (hours, policies, location, contact, FAQ)
- Parse TOON files at app startup using kotlin-toon library (br.com.vexpera:kotlin-toon:1.0.0)
- Validate TOON syntax on load with clear error messages if malformed
- Structure data for efficient LLM prompt inclusion (minimize token usage)
- Use tabular array syntax for uniform data structures (menu items, FAQ entries)

**Settings Screen**
- Create accessible settings screen with Jetpack Compose
- Manual Activation Button: Large prominent button to manually trigger conversation (alternative to "Hey, Temi" wake word)
- Wake Word Toggle: Enable/disable wake word detection ("Hey, Temi")
- Wake Word Sensitivity: Slider to adjust sensitivity (Low/Medium/High) to reduce false positives in noisy environments
- TTS Speech Rate: Slider to adjust speaking speed (50-250 words per minute, default 150)
- Display Debug Info: Toggle to show transcription text and LLM response on screen for debugging
- Status Indicator Size: Slider to adjust circle size (40-80% of screen, default 60%)
- Interruption Detection: Toggle to enable/disable interruption handling
- Settings persistence using Android SharedPreferences or DataStore
- Accessible via gesture or menu button (integration with Temi SDK navigation TBD)

**Clean Architecture Implementation**
- Organize code into Presentation (Compose UI + ViewModels), Domain (Use Cases), Data (Repositories) layers
- Create separate modules: WakeWordDetector, SttService, LlmService, TtsService, InterruptionHandler, ToonDataLoader, SettingsRepository
- Use Koin for dependency injection throughout
- Follow MVVM pattern with ViewModels managing UI state via Kotlin Flow
- Use Kotlin Coroutines for all async operations (no blocking on main thread)
- SettingsRepository manages settings persistence using DataStore (preferred) or SharedPreferences

**Automated Testing for Critical Scenarios**
- Wake word detection: verify activation on "Hey, Temi", verify manual button activation, verify acceptable false positive rate, verify wake word toggle disables listening
- Speech recognition: verify accurate transcription of common menu queries, verify timeout behavior
- LLM responses: verify accurate menu answers, verify TOON data loading via kotlin-toon library, verify out-of-scope handling
- Interruption handling: verify TTS stops on user speech, verify status indicator transitions, verify state reset, verify interruption toggle disables monitoring
- Status indicator: verify all three states display correctly, verify smooth transitions, verify fullscreen rendering, verify size adjustments from settings
- Settings screen: verify all settings persist correctly, verify manual activation button triggers conversation, verify settings changes are applied to relevant services
- End-to-end flows: wake word to response completion, interruption flow with reevaluation, manual activation to response
- Use MockK for mocking Gemma responses to speed up test execution
- Use Jetpack Compose UI Testing for status indicator and settings screen UI verification

## Visual Design

No visual mockups provided. UI consists of two main screens: fullscreen pulsating circle status indicator and settings screen.

**Status Indicator Visual Specifications**
- Fullscreen circular shape centered on display
- Idle state: grey (hex #808080) static circle at 100% scale
- Listening/Thinking state: red (hex #FF0000) pulsating circle with breathing animation (1.0 to 1.15 scale, 1.5s cycle)
- Speaking state: green (hex #00FF00) pulsating circle with breathing animation (1.0 to 1.15 scale, 1.5s cycle)
- Smooth color transitions between states (300ms animated lerp)
- Circle size: configurable 40-80% of screen width/height (whichever is smaller), default 60%
- Background: black (hex #000000) for high contrast
- Debug overlay: when enabled, show white text overlays for transcription and LLM response

**Settings Screen Visual Specifications**
- Standard Material Design 3 settings layout with Jetpack Compose
- Large manual activation button at top (primary action, distinct visual emphasis)
- Settings organized into sections: Activation, Speech, Display, Debug
- Toggles: Material3 Switch components
- Sliders: Material3 Slider components with value labels
- Accessible via navigation gesture or menu (Temi SDK integration TBD)
- Settings persist across app restarts

## Existing Code to Leverage

**No Existing Code Found**
- This is a greenfield implementation with no similar features in the current codebase
- All components will be built from scratch following Clean Architecture and Android best practices
- Follow tech stack conventions: Kotlin, Gradle, Jetpack Compose, Koin, Coroutines, Flow
- Reference agent-os/standards/ for coding style, error handling, testing practices

**TOON Format Parser**
- Use kotlin-toon library (https://github.com/vexpera-br/kotlin-toon) for parsing TOON files
- Add dependency: `implementation("br.com.vexpera:kotlin-toon:1.0.0")`
- Do NOT create custom TOON parser implementation
- Use `Toon.decode()` to parse menu.toon and knowledge-base.toon files
- Cache parsed data structure for efficient LLM prompt construction
- Example usage:
  ```kotlin
  val menuText = assets.open("menu.toon").bufferedReader().use { it.readText() }
  val menuData = Toon.decode(menuText)
  val dishes = menuData["dishes"]?.asListOf<Dish>()
  ```

## Out of Scope

- Backend API or server infrastructure of any kind
- Database (PostgreSQL, Room, SQLite, or any persistent storage beyond in-memory caching)
- User authentication or authorization systems
- Multi-restaurant support or tenant management
- Cloud LLM integration or fallback (no OpenAI GPT-4o, no hybrid architecture)
- Robot-to-restaurant assignment or configuration system
- Role-based permissions (Admin, Manager, Staff roles)
- Analytics tracking, monitoring, or business intelligence features
- Admin dashboard or web interface
- Multilingual support (Spanish, Portuguese) - English only for Phase 1
- Dynamic menu updates (data is hardcoded in assets folder, no live editing)
- Network connectivity or online features (fully offline operation)
- User management or multi-tenancy
- Restaurant management CRUD operations or content management system
