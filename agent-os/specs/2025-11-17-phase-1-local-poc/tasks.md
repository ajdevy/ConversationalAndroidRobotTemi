# Task Breakdown: Phase 1 - Local Conversational Robot PoC

## Overview
Total Task Groups: 8
Total Tasks: 58 sub-tasks

**Project Scope:** Build a fully functional on-device conversational robot for Temi using local LLM (Gemma 3n-E2B), STT, and TTS to answer restaurant menu and information questions with no cloud dependencies.

**Key Technologies:** Android (Kotlin), Jetpack Compose, Gemma 3n-E2B (Google AI Edge SDK), Temi SDK, TOON format data files, Koin DI, Coroutines, Flow

## Task List

### Project Setup & Dependencies

#### Task Group 1: Initial Project Configuration
**Dependencies:** None

- [x] 1.0 Complete project setup and dependency configuration
  - [x] 1.1 Create Android project structure
    - Initialize Kotlin Android project with Gradle
    - Configure minimum SDK version compatible with Temi robot
    - Set up project package structure: `com.temi.conversationalrobot`
    - Configure Gradle Kotlin DSL (build.gradle.kts)
  - [x] 1.2 Configure Clean Architecture module structure
    - Create module structure: `app`, `presentation`, `domain`, `data`
    - Set up module dependencies in Gradle
    - Configure module-specific build files
  - [x] 1.3 Add core dependencies to build.gradle.kts
    - Add Jetpack Compose BOM and core libraries (compose-ui, compose-material3, compose-animation)
    - Add Koin dependency injection (koin-android, koin-androidx-compose)
    - Add Kotlin Coroutines (kotlinx-coroutines-android)
    - Add Kotlin Flow (kotlinx-coroutines-core)
    - Add AndroidX Core KTX and Lifecycle libraries
  - [x] 1.4 Add AI/ML and Temi-specific dependencies
    - Add Google AI Edge SDK for Gemma 3n-E2B (com.google.ai.edge:generativeai)
    - Add Temi SDK (com.robotemi:sdk) - verify latest version
    - Add kotlin-toon library (br.com.vexpera:kotlin-toon:1.0.0)
    - Add wake word detection library (Porcupine or alternative - evaluate and select)
  - [x] 1.5 Add testing dependencies
    - Add JUnit5 for unit tests
    - Add MockK for Kotlin mocking (io.mockk:mockk)
    - Add Jetpack Compose UI Testing (androidx.compose.ui:ui-test-junit4)
    - Add Kotlin Coroutines Test (kotlinx-coroutines-test)
    - Add Turbine for Flow testing (app.cash.turbine:turbine)
  - [x] 1.6 Configure Android Manifest
    - Add required permissions: RECORD_AUDIO, INTERNET (for initial setup only)
    - Configure Temi SDK initialization in manifest
    - Set up main activity with fullscreen theme
    - Add settings activity declaration
  - [x] 1.7 Set up Koin dependency injection
    - Create Koin application module file (`di/AppModule.kt`)
    - Initialize Koin in Application class
    - Configure module structure for layered DI (data, domain, presentation modules)

**Acceptance Criteria:**
- Project builds successfully without errors
- All dependencies resolve correctly
- Koin DI initializes without errors
- Clean Architecture module structure is in place
- Android Manifest contains required permissions

---

### Data Layer

#### Task Group 2: TOON Data Files and Parser Integration
**Dependencies:** Task Group 1

- [x] 2.0 Complete TOON data layer implementation
  - [x] 2.1 Write 2-8 focused tests for TOON data loading
    - Test successful parsing of menu.toon file
    - Test successful parsing of knowledge-base.toon file
    - Test error handling for malformed TOON syntax
    - Test data structure validation (expected fields present)
  - [x] 2.2 Create menu.toon data file
    - Create file at `app/src/main/assets/menu.toon`
    - Structure menu data using TOON tabular array syntax
    - Include fields: dish name, price, description, ingredients, allergens, category
    - Create sample data for 15-20 restaurant menu items
    - Optimize for token efficiency (minimize whitespace, use abbreviations where clear)
  - [x] 2.3 Create knowledge-base.toon data file
    - Create file at `app/src/main/assets/knowledge-base.toon`
    - Include restaurant information: hours, location, contact, policies
    - Include FAQ entries: common questions and answers
    - Structure using TOON format for efficient LLM consumption
  - [x] 2.4 Implement ToonDataLoader service
    - Create `ToonDataLoader.kt` in data layer
    - Implement file loading from assets folder
    - Integrate kotlin-toon library for parsing (Toon.decode())
    - Cache parsed data structure in memory
    - Validate TOON syntax and throw clear exceptions if malformed
  - [x] 2.5 Create data models for parsed TOON content
    - Create `MenuData.kt`: data class for menu structure
    - Create `KnowledgeBaseData.kt`: data class for knowledge base structure
    - Define fields matching TOON file structure
    - Implement extension functions to convert TOON decoded objects to data models
  - [x] 2.6 Create ToonRepository
    - Implement repository pattern for TOON data access
    - Expose menu and knowledge base data via Kotlin Flow
    - Handle loading errors gracefully with sealed result types
    - Register in Koin DI module
  - [x] 2.7 Ensure TOON data layer tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify menu.toon and knowledge-base.toon parse successfully
    - Verify data models populate correctly

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- menu.toon and knowledge-base.toon files exist with valid sample data
- kotlin-toon library successfully parses both files
- ToonRepository exposes data via Flow
- Data models correctly represent TOON structure

---

### Settings Layer

#### Task Group 3: Settings Storage and Repository
**Dependencies:** Task Group 1

- [x] 3.0 Complete settings persistence layer
  - [x] 3.1 Write 2-8 focused tests for settings repository
    - Test default settings values on first launch
    - Test settings persistence across repository recreation
    - Test settings update and retrieval
    - Test all setting types (Boolean, Float, Int)
  - [x] 3.2 Define settings data model
    - Create `AppSettings.kt` data class
    - Fields: wakeWordEnabled (Boolean), wakeWordSensitivity (Int: 0-2 for Low/Med/High), ttsSpeechRate (Float: 50-250), debugInfoEnabled (Boolean), statusIndicatorSize (Float: 0.4-0.8), interruptionDetectionEnabled (Boolean)
    - Provide default values matching spec (ttsSpeechRate=150, statusIndicatorSize=0.6, etc.)
  - [x] 3.3 Implement SettingsRepository with DataStore
    - Create `SettingsRepository.kt` in data layer
    - Use Jetpack DataStore Preferences for persistence
    - Implement functions: getSettings() -> Flow<AppSettings>, updateWakeWordEnabled(), updateWakeWordSensitivity(), updateTtsSpeechRate(), updateDebugInfo(), updateStatusIndicatorSize(), updateInterruptionDetection()
    - Expose settings as StateFlow for reactive UI updates
  - [x] 3.4 Register SettingsRepository in Koin DI
    - Add SettingsRepository to data module
    - Provide DataStore instance as singleton
  - [x] 3.5 Ensure settings layer tests pass
    - Run ONLY the 2-8 tests written in 3.1
    - Verify settings persist across app restarts
    - Verify all settings update correctly

**Acceptance Criteria:**
- The 2-8 tests written in 3.1 pass
- Settings persist using DataStore
- Settings exposed as StateFlow
- Default values match spec requirements
- SettingsRepository registered in Koin

---

### Core Services Layer

#### Task Group 4: Wake Word Detection Service
**Dependencies:** Task Group 1, Task Group 3 (settings)

- [x] 4.0 Complete wake word detection implementation
  - [x] 4.1 Write 2-8 focused tests for wake word service
    - Test wake word detection triggers callback on "Hey, Temi"
    - Test wake word service respects enabled/disabled toggle from settings
    - Test sensitivity adjustment affects detection threshold
    - Test service stops listening when disabled
  - [x] 4.2 Evaluate and select wake word detection library
    - Research options: Porcupine, Snowboy, custom implementation
    - Evaluate licensing, accuracy, Android compatibility, CPU efficiency
    - Select library and document decision
    - Add chosen library dependency to build.gradle.kts
  - [x] 4.3 Implement WakeWordDetector service
    - Create `WakeWordDetector.kt` in data layer
    - Initialize wake word engine with "Hey, Temi" keyword
    - Implement background audio listening (AudioRecord or library API)
    - Expose detection events via Kotlin Flow
    - Implement sensitivity configuration (Low/Medium/High thresholds)
    - Implement CPU-efficient audio processing (avoid continuous processing, use VAD if available)
  - [x] 4.4 Implement wake word lifecycle management
    - Start/stop listening based on settings.wakeWordEnabled
    - Release audio resources when disabled
    - Handle Android audio focus properly
    - Implement service binding for lifecycle-aware activation
  - [x] 4.5 Integrate with SettingsRepository
    - Subscribe to settings changes (wakeWordEnabled, wakeWordSensitivity)
    - Dynamically update detection behavior based on settings
  - [x] 4.6 Add permissions handling for RECORD_AUDIO
    - Implement runtime permission request in presentation layer
    - Disable wake word detection if permission denied
  - [x] 4.7 Ensure wake word service tests pass
    - Run ONLY the 2-8 tests written in 4.1
    - Verify detection works with mock audio input
    - Verify settings integration

**Acceptance Criteria:**
- The 2-8 tests written in 4.1 pass
- Wake word library selected and integrated
- WakeWordDetector detects "Hey, Temi" with <100ms latency
- Service respects settings for enabled/disabled and sensitivity
- Audio resources properly managed (no leaks)

---

#### Task Group 5: Speech-to-Text Service
**Dependencies:** Task Group 1, Task Group 3 (settings)

- [ ] 5.0 Complete STT service implementation
  - [ ] 5.1 Write 2-8 focused tests for STT service
    - Test successful transcription with mock SpeechRecognizer
    - Test timeout handling (5 seconds silence)
    - Test error handling for unclear speech
    - Test service lifecycle (start/stop listening)
  - [ ] 5.2 Implement SttService using Google Speech Recognition API
    - Create `SttService.kt` in data layer
    - Initialize android.speech.SpeechRecognizer
    - Configure for on-device recognition (no network)
    - Set language to English (Locale.ENGLISH)
  - [ ] 5.3 Implement transcription lifecycle
    - Implement startListening() function
    - Implement stopListening() function
    - Handle RecognitionListener callbacks (onResults, onError, onEndOfSpeech)
    - Expose transcription results via Kotlin Flow
  - [ ] 5.4 Implement timeout handling
    - Add 5-second silence timeout using Coroutines delay
    - Emit timeout event via Flow if user doesn't speak
    - Stop listening and release resources on timeout
  - [ ] 5.5 Implement error handling
    - Handle SpeechRecognizer.ERROR_NO_MATCH (unclear speech)
    - Handle SpeechRecognizer.ERROR_NETWORK (should not occur, on-device only)
    - Emit error events via Flow with clear error types
    - Implement retry logic for transient errors
  - [ ] 5.6 Register SttService in Koin DI
    - Add to data module
    - Provide SpeechRecognizer instance
  - [ ] 5.7 Ensure STT service tests pass
    - Run ONLY the 2-8 tests written in 5.1
    - Verify transcription flow works
    - Verify timeout and error handling

**Acceptance Criteria:**
- The 2-8 tests written in 5.1 pass
- SttService transcribes speech using Google on-device API
- Transcription latency <2 seconds for typical utterances
- Timeout and error handling work correctly
- Service registered in Koin

---

#### Task Group 6: LLM Service and Prompt Engineering
**Dependencies:** Task Group 1, Task Group 2 (TOON data), Task Group 3 (settings)

- [ ] 6.0 Complete LLM service implementation
  - [ ] 6.1 Write 2-8 focused tests for LLM service
    - Test LLM generates response for menu question (mock Gemma API)
    - Test LLM handles out-of-scope questions gracefully
    - Test timeout handling (10 seconds max)
    - Test TOON data injection into prompt context
  - [ ] 6.2 Implement LlmService using Google AI Edge SDK
    - Create `LlmService.kt` in data layer
    - Initialize Gemma 3n-E2B model using AI Edge SDK
    - Load model at app startup and keep in memory
    - Configure inference parameters (temperature, max tokens, etc.)
  - [ ] 6.3 Implement prompt engineering for menu QA
    - Create `PromptBuilder.kt` utility
    - Design system prompt: "You are a helpful restaurant assistant. Answer questions about our menu and restaurant information based ONLY on the provided data. If you don't know the answer, politely say so."
    - Inject TOON menu data into prompt context
    - Inject TOON knowledge base data into prompt context
    - Optimize prompt for token efficiency (minimize redundant text)
  - [ ] 6.4 Implement generateResponse function
    - Accept user utterance as input
    - Build complete prompt with system context + TOON data + user question
    - Call Gemma 3n-E2B inference API
    - Return response as String via Flow
    - Handle streaming responses if supported by SDK
  - [ ] 6.5 Implement timeout and error handling
    - Add 10-second timeout using withTimeout coroutine
    - Fallback to "I'm not sure, could you rephrase?" if timeout
    - Handle out-of-memory errors gracefully (clear cache, retry)
    - Emit errors via Flow with clear error types
  - [ ] 6.6 Integrate with ToonRepository
    - Inject ToonRepository via Koin
    - Subscribe to menu and knowledge base data Flow
    - Update prompt context when data changes (though data is static in Phase 1)
  - [ ] 6.7 Register LlmService in Koin DI
    - Add to data module
    - Provide Gemma model instance as singleton (memory-resident)
  - [ ] 6.8 Ensure LLM service tests pass
    - Run ONLY the 2-8 tests written in 6.1
    - Use mocked Gemma responses for fast test execution
    - Verify prompt includes TOON data
    - Verify timeout and error handling

**Acceptance Criteria:**
- The 2-8 tests written in 6.1 pass
- LlmService generates responses using Gemma 3n-E2B
- Inference latency <3 seconds for typical questions
- TOON data correctly injected into prompts
- Out-of-scope questions handled gracefully
- Service registered in Koin

---

#### Task Group 7: Text-to-Speech and Interruption Services
**Dependencies:** Task Group 1, Task Group 3 (settings)

- [ ] 7.0 Complete TTS and interruption handling implementation
  - [ ] 7.1 Write 2-8 focused tests for TTS and interruption
    - Test TTS speaks text with correct speech rate from settings
    - Test TTS fallback to text display if engine fails
    - Test interruption detection stops TTS immediately
    - Test interruption detection respects enabled/disabled toggle
  - [ ] 7.2 Implement TtsService using Android TextToSpeech API
    - Create `TtsService.kt` in data layer
    - Initialize android.speech.tts.TextToSpeech
    - Configure English language pack
    - Implement speak() function with speech rate parameter
  - [ ] 7.3 Integrate TTS with SettingsRepository
    - Subscribe to settings.ttsSpeechRate Flow
    - Dynamically update speech rate (50-250 WPM, default 150)
    - Map WPM to TTS speed value (0.5 to 2.5 range)
  - [ ] 7.4 Implement TTS lifecycle and status tracking
    - Track speaking state (idle, speaking, paused)
    - Expose speaking state via StateFlow
    - Implement stop() function to interrupt speech
    - Implement TTS UtteranceProgressListener for completion callbacks
    - Emit completion events via Flow
  - [ ] 7.5 Implement fallback to text display
    - Detect TTS initialization failures
    - Emit "display text" events via Flow if TTS unavailable
    - Log TTS errors clearly for debugging
  - [ ] 7.6 Implement InterruptionHandler service
    - Create `InterruptionHandler.kt` in data layer
    - Monitor audio input while TTS is speaking (use AudioRecord or VAD)
    - Detect user speech via amplitude threshold or Voice Activity Detection
    - Target <200ms latency from user speech to detection
  - [ ] 7.7 Integrate interruption detection with TTS
    - Subscribe to TTS speaking state in InterruptionHandler
    - Only monitor audio when TTS is actively speaking AND interruptionDetectionEnabled=true
    - Stop TTS immediately when user speech detected
    - Emit interruption events via Flow
  - [ ] 7.8 Implement audio pipeline coordination
    - Ensure wake word detection, STT, and interruption monitoring don't conflict
    - Implement audio focus management (prioritize active operation)
    - Release audio resources properly when switching operations
  - [ ] 7.9 Register services in Koin DI
    - Add TtsService to data module
    - Add InterruptionHandler to data module
  - [ ] 7.10 Ensure TTS and interruption tests pass
    - Run ONLY the 2-8 tests written in 7.1
    - Mock TTS engine for fast test execution
    - Verify speech rate settings integration
    - Verify interruption detection works

**Acceptance Criteria:**
- The 2-8 tests written in 7.1 pass
- TtsService speaks responses with configurable rate
- Interruption detection stops TTS within 200ms of user speech
- Audio pipeline properly coordinated (no conflicts)
- Fallback to text display works if TTS fails
- Services registered in Koin

---

### Domain Layer

#### Task Group 8: Use Cases and Conversation Orchestration
**Dependencies:** Task Groups 4, 5, 6, 7 (all services)

- [ ] 8.0 Complete domain use cases
  - [ ] 8.1 Write 2-8 focused tests for use cases
    - Test HandleConversationUseCase end-to-end flow (STT -> LLM -> TTS)
    - Test interruption flow (TTS speaking -> user interrupts -> re-evaluate)
    - Test timeout handling in conversation flow
    - Test manual activation trigger
  - [ ] 8.2 Create HandleConversationUseCase
    - Create `HandleConversationUseCase.kt` in domain layer
    - Orchestrate conversation flow: activate -> listen (STT) -> think (LLM) -> speak (TTS)
    - Emit conversation state events via Flow (Idle, Listening, Thinking, Speaking)
    - Handle errors from each service layer
    - Coordinate wake word and manual activation triggers
  - [ ] 8.3 Implement conversation state management
    - Define ConversationState sealed class (Idle, Listening, Thinking, Speaking, Error)
    - Maintain current state using StateFlow
    - Implement state transitions with validation (e.g., can't go from Idle to Speaking)
  - [ ] 8.4 Implement HandleInterruptionUseCase
    - Create `HandleInterruptionUseCase.kt` in domain layer
    - Listen for interruption events from InterruptionHandler
    - Stop TTS immediately on interruption
    - Reset conversation state to Listening
    - Re-trigger STT to capture interrupting speech
    - Feed interruption context to LLM for response re-evaluation
  - [ ] 8.5 Create ActivateConversationUseCase
    - Create `ActivateConversationUseCase.kt` in domain layer
    - Handle both wake word and manual button activation
    - Validate preconditions (audio permissions, services initialized)
    - Transition state from Idle to Listening
    - Trigger STT service
  - [ ] 8.6 Register use cases in Koin DI
    - Add to domain module
    - Inject required services (SttService, LlmService, TtsService, InterruptionHandler)
  - [ ] 8.7 Ensure use case tests pass
    - Run ONLY the 2-8 tests written in 8.1
    - Verify end-to-end conversation flows
    - Verify interruption handling flow

**Acceptance Criteria:**
- The 2-8 tests written in 8.1 pass
- HandleConversationUseCase orchestrates full conversation flow
- HandleInterruptionUseCase stops and re-evaluates on user interruption
- Conversation state managed correctly via StateFlow
- Use cases registered in Koin

---

### Presentation Layer - UI Components

#### Task Group 9: Status Indicator Component
**Dependencies:** Task Group 8 (use cases for state)

- [ ] 9.0 Complete status indicator UI implementation
  - [ ] 9.1 Write 2-8 focused tests for status indicator
    - Test idle state displays grey static circle
    - Test listening/thinking state displays red pulsating circle
    - Test speaking state displays green pulsating circle
    - Test smooth transitions between states
    - Test circle size adjusts based on settings
  - [ ] 9.2 Create StatusIndicatorViewModel
    - Create `StatusIndicatorViewModel.kt` in presentation layer
    - Inject HandleConversationUseCase and SettingsRepository via Koin
    - Subscribe to conversation state Flow from use case
    - Subscribe to settings Flow for statusIndicatorSize and debugInfoEnabled
    - Expose UI state via StateFlow: currentState (Idle/Listening/Speaking), indicatorSize, debugInfo
  - [ ] 9.3 Implement StatusIndicatorScreen composable
    - Create `StatusIndicatorScreen.kt` in presentation layer
    - Implement fullscreen Box with black background (#000000)
    - Implement circular Canvas drawing in center
    - Use Jetpack Compose Animation for pulsating effect
    - Map conversation state to colors: Idle=#808080, Listening/Thinking=#FF0000, Speaking=#00FF00
  - [ ] 9.4 Implement pulsating breathing animation
    - Use Compose InfiniteTransition for continuous animation
    - Animate circle scale from 1.0 to 1.15 over 1.5 second cycle
    - Use EaseInOut easing for smooth breathing effect
    - Only animate when state is Listening/Thinking or Speaking (static when Idle)
  - [ ] 9.5 Implement smooth color transitions
    - Use Compose animateColorAsState for state color changes
    - Set animation duration to 300ms with FastOutSlowIn easing
  - [ ] 9.6 Implement debug info overlay
    - When debugInfoEnabled=true, overlay white text on screen
    - Display current transcription text (from STT)
    - Display current LLM response text
    - Use contrasting white text (#FFFFFF) with semi-transparent background
  - [ ] 9.7 Implement size adjustments from settings
    - Calculate circle diameter based on settings.statusIndicatorSize (40-80% of screen, default 60%)
    - Use min(screenWidth, screenHeight) * indicatorSize for diameter
    - Ensure circle stays centered and fullscreen
  - [ ] 9.8 Ensure status indicator UI tests pass
    - Run ONLY the 2-8 tests written in 9.1
    - Use Compose UI Testing framework
    - Verify all three states render correctly
    - Verify animations work

**Acceptance Criteria:**
- The 2-8 tests written in 9.1 pass
- Status indicator displays fullscreen pulsating circle
- Three states (Idle, Listening/Thinking, Speaking) visually distinct
- Smooth color transitions and breathing animation
- Size adjustable via settings
- Debug overlay works when enabled

---

#### Task Group 10: Settings Screen UI
**Dependencies:** Task Group 3 (SettingsRepository), Task Group 8 (ActivateConversationUseCase)

- [ ] 10.0 Complete settings screen implementation
  - [ ] 10.1 Write 2-8 focused tests for settings screen
    - Test all settings controls render correctly
    - Test manual activation button triggers conversation
    - Test settings changes persist
    - Test settings changes update repository
  - [ ] 10.2 Create SettingsViewModel
    - Create `SettingsViewModel.kt` in presentation layer
    - Inject SettingsRepository and ActivateConversationUseCase via Koin
    - Subscribe to settings Flow from repository
    - Expose UI state via StateFlow with all settings values
    - Implement update functions for each setting
  - [ ] 10.3 Implement SettingsScreen composable
    - Create `SettingsScreen.kt` in presentation layer
    - Use Material3 Scaffold with TopAppBar
    - Organize settings into sections with headers
  - [ ] 10.4 Implement manual activation button
    - Large prominent Material3 Button at top of screen
    - Label: "Start Conversation" or "Talk to Temi"
    - On click: trigger ActivateConversationUseCase
    - Disable button when conversation is active (not Idle state)
  - [ ] 10.5 Implement Activation settings section
    - Section header: "Activation"
    - Wake Word Toggle: Material3 Switch for wakeWordEnabled
    - Wake Word Sensitivity: Material3 Slider with 3 discrete steps (Low=0, Medium=1, High=2)
    - Display current sensitivity label below slider
  - [ ] 10.6 Implement Speech settings section
    - Section header: "Speech"
    - TTS Speech Rate: Material3 Slider (50-250 WPM, default 150)
    - Display current WPM value below slider
  - [ ] 10.7 Implement Display settings section
    - Section header: "Display"
    - Status Indicator Size: Material3 Slider (0.4-0.8, default 0.6)
    - Display percentage value below slider (e.g., "60%")
  - [ ] 10.8 Implement Debug settings section
    - Section header: "Debug"
    - Display Debug Info: Material3 Switch for debugInfoEnabled
    - Interruption Detection: Material3 Switch for interruptionDetectionEnabled
  - [ ] 10.9 Implement settings persistence
    - On each setting change, call ViewModel update function
    - ViewModel updates SettingsRepository
    - Repository persists to DataStore
  - [ ] 10.10 Implement navigation to/from settings
    - Add navigation from StatusIndicatorScreen to SettingsScreen
    - Use bottom-right FAB (Floating Action Button) on status indicator for settings access
    - Use Jetpack Compose Navigation or simple state-based screen switching
  - [ ] 10.11 Ensure settings screen UI tests pass
    - Run ONLY the 2-8 tests written in 10.1
    - Use Compose UI Testing framework
    - Verify all controls render and interact correctly
    - Verify manual activation button works

**Acceptance Criteria:**
- The 2-8 tests written in 10.1 pass
- Settings screen displays all controls matching spec
- Manual activation button triggers conversation
- All settings persist across app restarts
- Navigation to/from settings works
- UI follows Material3 design guidelines

---

### Integration and Testing

#### Task Group 11: End-to-End Integration Testing
**Dependencies:** All previous task groups

- [ ] 11.0 Complete integration testing and critical gap analysis
  - [ ] 11.1 Review existing tests from Task Groups 2-10
    - Review the 2-8 tests written in each previous task group (2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 10.1)
    - Verify approximately 18-72 tests exist covering individual components
  - [ ] 11.2 Analyze test coverage gaps for critical user workflows
    - Identify gaps in wake word -> STT -> LLM -> TTS end-to-end flow
    - Identify gaps in interruption detection and handling flow
    - Identify gaps in manual activation flow
    - Identify gaps in settings persistence and application
    - Focus ONLY on gaps in THIS feature's critical paths
  - [ ] 11.3 Write up to 10 additional integration tests maximum
    - End-to-end wake word activation flow test
    - End-to-end manual button activation flow test
    - Full conversation flow: activation -> listen -> think -> speak -> complete
    - Interruption flow: speaking -> user interrupts -> TTS stops -> re-evaluate -> new response
    - Settings change application: update setting -> verify service behavior changes
    - Audio pipeline coordination: verify no conflicts between wake word, STT, interruption monitoring
    - Timeout scenarios: STT timeout, LLM timeout
    - Error recovery: STT error -> retry, LLM error -> fallback
    - TOON data loading integration: verify menu and knowledge base accessible to LLM
    - Memory pressure handling: verify app stays within 2.5-3GB memory limit
  - [ ] 11.4 Run all feature tests and verify passing
    - Run all tests written across task groups 2-11
    - Expected total: approximately 28-82 tests maximum
    - Verify critical workflows pass
    - Fix any failures

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 28-82 tests total)
- Critical end-to-end workflows covered
- No more than 10 additional integration tests added
- All tests execute in reasonable time (<5 minutes total)
- App memory footprint verified <3GB during testing

---

### Performance Optimization and Polish

#### Task Group 12: Performance Validation and Final Polish
**Dependencies:** Task Group 11 (integration tests passing)

- [ ] 12.0 Complete performance validation and final polish
  - [ ] 12.1 Validate performance requirements on Temi hardware
    - Test wake word detection latency: verify <100ms from speech to activation
    - Test STT transcription latency: verify <2 seconds for typical utterances
    - Test LLM inference latency: verify <3 seconds for typical menu questions
    - Test interruption detection latency: verify <200ms from user speech to TTS stop
    - Test TTS natural pace: verify 150 WPM default is natural sounding
    - Profile with Android Profiler on actual Temi device
  - [ ] 12.2 Validate memory footprint
    - Monitor app memory usage with Android Profiler
    - Verify Gemma 3n-E2B stays within ~2GB
    - Verify total app memory <3GB including UI, services, SDK overhead
    - Test memory stability over extended usage (30+ conversations)
    - Fix memory leaks if detected
  - [ ] 12.3 Optimize battery efficiency
    - Verify wake word background listening is CPU-efficient
    - Profile CPU usage during idle state (should be minimal)
    - Optimize audio processing to avoid continuous polling
    - Implement proper wake locks and audio focus management
  - [ ] 12.4 Validate Temi SDK integration
    - Test that Compose UI renders correctly on Temi display
    - Verify no conflicts between Temi SDK audio and app audio pipeline
    - Test navigation integration (if Temi gesture navigation used)
    - Verify app responds correctly to Temi robot lifecycle events
  - [ ] 12.5 Polish UI/UX details
    - Fine-tune pulsating animation timing for natural breathing effect
    - Verify color contrast and readability on Temi display
    - Test settings screen layout on Temi screen size
    - Add haptic feedback for manual activation button (if Temi supports)
    - Ensure smooth transitions between screens
  - [ ] 12.6 Implement error message user feedback
    - Add user-friendly error messages for common failures
    - Display toast or dialog when permissions denied
    - Display message when STT times out: "I didn't hear you, please try again"
    - Display message when LLM times out: "I'm thinking... please wait"
    - Ensure error messages are clear and actionable
  - [ ] 12.7 Add logging and debugging support
    - Implement structured logging for all services (wake word, STT, LLM, TTS, interruption)
    - Log performance metrics (latencies) for debugging
    - Ensure debug logs don't leak sensitive data
    - Add crash reporting (optional, if analytics framework available)
  - [ ] 12.8 Final end-to-end validation
    - Test complete conversation flows on actual Temi hardware
    - Test in realistic noisy restaurant environment (if possible)
    - Verify wake word accuracy in noisy conditions
    - Test interruption handling with realistic user behavior
    - Validate all settings controls work as expected
    - Ensure app is stable over extended usage

**Acceptance Criteria:**
- All performance requirements met (<100ms wake word, <2s STT, <3s LLM, <200ms interruption)
- Memory footprint <3GB verified on Temi hardware
- Battery-efficient background listening confirmed
- Temi SDK integration works without conflicts
- UI/UX polished and professional
- Error messages clear and user-friendly
- App stable in extended testing

---

## Execution Order

Recommended implementation sequence:

1. **Project Setup & Dependencies** (Task Group 1) - Foundation for all work
2. **Data Layer** (Task Group 2) - TOON data files and parsing needed for LLM
3. **Settings Layer** (Task Group 3) - Settings infrastructure needed by all services
4. **Wake Word Detection** (Task Group 4) - First activation mechanism
5. **Speech-to-Text** (Task Group 5) - Core input processing
6. **LLM Service** (Task Group 6) - Core intelligence and response generation
7. **TTS and Interruption** (Task Group 7) - Core output and interaction handling
8. **Domain Use Cases** (Task Group 8) - Orchestration layer tying services together
9. **Status Indicator UI** (Task Group 9) - Primary user interface
10. **Settings Screen UI** (Task Group 10) - Secondary interface and manual activation
11. **Integration Testing** (Task Group 11) - Validate end-to-end flows
12. **Performance & Polish** (Task Group 12) - Final optimization and validation

---

## Important Notes

### Testing Strategy
- Each task group (2-10) writes **2-8 focused tests maximum** covering critical behaviors only
- Tests run ONLY the newly written tests for that group, not the entire suite
- Task Group 11 adds **up to 10 additional integration tests** to fill critical gaps
- Total expected tests: approximately **28-82 tests** for the entire feature
- Use MockK for service mocking, Compose UI Testing for UI validation
- Mock Gemma responses for fast test execution

### Architecture Principles
- **Clean Architecture**: Strict separation of Presentation, Domain, Data layers
- **MVVM Pattern**: ViewModels manage UI state via StateFlow
- **Dependency Injection**: Koin for all service dependencies
- **Reactive Streams**: Kotlin Flow for async data streams
- **Coroutines**: All async operations use structured concurrency

### Performance Targets
- Wake word detection: <100ms latency
- STT transcription: <2 seconds
- LLM inference: <3 seconds
- Interruption detection: <200ms
- TTS speech rate: 150 WPM default (configurable 50-250 WPM)
- Memory footprint: <3GB total

### Key Libraries and Dependencies
- **kotlin-toon**: `br.com.vexpera:kotlin-toon:1.0.0` for TOON parsing
- **Google AI Edge SDK**: for Gemma 3n-E2B LLM inference
- **Temi SDK**: `com.robotemi:sdk` for robot integration
- **Jetpack Compose**: Material3 for UI
- **Koin**: for dependency injection
- **DataStore**: for settings persistence
- **Wake word library**: Porcupine or alternative (TBD in Task 4.2)

### Audio Pipeline Coordination
- Critical to avoid conflicts between wake word detection, STT, interruption monitoring, and TTS
- Implement audio focus management to prioritize active operation
- Release audio resources when not in use
- Test thoroughly on actual Temi hardware

### TOON Format Benefits
- 30-60% token reduction vs JSON for LLM prompts
- Indentation-based syntax similar to YAML
- Tabular arrays for uniform data structures
- Minimal syntax overhead
- Human-readable for easier debugging
