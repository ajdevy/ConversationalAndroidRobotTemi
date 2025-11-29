# Spec Requirements: Phase 1 - Local PoC

## Initial Description

Build a working proof-of-concept conversational robot using:
- Android app with Temi SDK integration
- Local LLM (Gemma 2/3) for handling conversations
- Local speech recognition (STT) and text-to-speech (TTS)
- Hardcoded files inside the project for menu and knowledge base data (no backend/database)
- Visual status indicator (red=thinking, green=speaking, grey=idle)
- Ability to ask questions about the menu and knowledge base

**Goal:** Create a fully functional local conversational robot that can answer questions about a restaurant's menu and general information, running entirely on-device without requiring cloud services or backend infrastructure.

**Size:** L (Large) - This is a complete working PoC

## Requirements Discussion

### First Round Questions

**Q1: Conversation Activation**
How should users activate the conversation? Wake word detection ("Hey Temi"), tap-to-talk button, or proximity-based automatic activation?

**Answer:** Wake word "Hey, Temi" + also add a button in settings screen to activate manually

**Q2: Local LLM Selection**
Which specific Gemma model variant should we use? Gemma 2B (lightweight, ~4GB), Gemma 7B (better quality, ~14GB), or the newer Gemma 3 series? Consider Temi robot's available RAM.

**Answer:** Use Gemma 3n-E2B (2GB RAM footprint, mobile-optimized) with Google AI Edge SDK

**Q3: Local STT Technology**
For local speech-to-text, should we use Google's on-device Speech Recognition API (android.speech.SpeechRecognizer), Mozilla DeepSpeech, or Vosk offline recognition? Each has different accuracy/size tradeoffs.

**Answer:** Use Google's on-device Speech Recognition API (android.speech.SpeechRecognizer)

**Q4: Local TTS Technology**
For text-to-speech, should we use Android's built-in TextToSpeech API, Google's local TTS engine, or a third-party solution like eSpeak?

**Answer:** Use Android's built-in TextToSpeech API with English language pack

**Q5: Data Storage Format**
For the hardcoded menu and knowledge base files, what format should we use? JSON (common), YAML (human-readable), SQLite (queryable), or plain text with structured format?

**Answer:** Use TOON format (Token-Oriented Object Notation) - a token-efficient format designed for LLMs that reduces token usage by 30-60%. Store files in Android app's assets folder.
- TOON format info: https://github.com/toon-format/toon
- Indentation-based like YAML
- Tabular arrays for uniform data
- Minimal syntax (no braces/brackets)
- Example: `menu[3]{name,price,description}: burger,10.99,delicious beef burger`

**Q6: Multilingual Support**
Even though Phase 1 is local-only, should we prepare the data structure for future multilingual support (Spanish, Portuguese, English), or keep everything in English only for now?

**Answer:** English only for Phase 1 PoC

**Q7: Interruption Handling**
Should the robot implement the interruption detection and handling feature (stopping mid-speech when user speaks), or defer this to a later phase?

**Answer:** YES - implement real-time interruption detection (monitor audio while TTS is speaking, stop immediately if user speaks)

**Q8: Status Indicator Implementation**
For the visual status indicator (red/green/grey pulsating circle), should this be a fullscreen overlay, a corner widget, or part of the main UI? What animation style - subtle pulse, breathing effect, or ripple?

**Answer:** Fullscreen pulsating circle (Jetpack Compose animations)

**Q9: LLM Integration Approach**
Should we use Google's AI Edge SDK (recommended for Gemma on Android), MediaPipe LLM Inference API, or a lower-level ONNX Runtime integration?

**Answer:** Use Google AI Edge SDK (no preference, this is the official recommended approach)

**Q10: Testing Strategy**
Should we implement automated tests for critical scenarios (wake word detection, LLM responses, interruption handling), or rely on manual testing for this PoC?

**Answer:** Implement automated tests for critical scenarios (not just manual testing)

**Q11: Scope Boundaries**
Just to confirm the exclusions for Phase 1 - we are NOT building: backend API, database, user authentication, multi-restaurant support, cloud LLM fallback, robot-to-restaurant assignment, permissions system, or analytics tracking. Correct?

**Answer:** CONFIRMED - exclude backend API, database, auth, multi-restaurant, cloud LLM, robot assignment, permissions, analytics

### Existing Code to Reference

**Similar Features Identified:**
No similar existing features identified for reference. This is a new greenfield implementation.

### Additional Technical Context

**Gemma 3n-E2B Multimodal Capabilities:**
- Gemma 3n-E2B has native audio capabilities including built-in ASR (Automatic Speech Recognition)
- The model is multimodal and supports text, image, audio, and video inputs
- Despite these native capabilities, we will use separate Google Speech Recognition API for STT in Phase 1 as planned for better modularity and easier debugging

**Memory Requirements:**
- Gemma 3n-E2B: ~2GB RAM footprint (mobile-optimized)
- Expected total app memory usage: ~2.5-3GB including UI, SDK, and runtime overhead
- Temi robot specifications should be verified to ensure adequate RAM availability

**TOON Format Benefits:**
- Token-efficient format specifically designed for LLM prompts
- Reduces token usage by 30-60% compared to JSON
- Perfect for feeding menu data to Gemma with minimal context window consumption
- Human-readable for easier debugging and content updates

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
No visual assets were added to the visuals folder. All UI specifications are based on textual descriptions provided in the requirements discussion.

## Requirements Summary

### Functional Requirements

**Core Conversation Flow:**
- User activates conversation via wake word "Hey, Temi" OR manual button in settings
- Robot displays fullscreen pulsating circle status indicator throughout interaction:
  - Grey static circle: Idle, ready for interaction
  - Red pulsating circle: Listening and processing (STT + LLM thinking)
  - Green pulsating circle: Speaking (TTS output)
- Robot listens via Google on-device Speech Recognition API
- Transcribed text is sent to Gemma 3n-E2B LLM via Google AI Edge SDK
- LLM generates response using menu and knowledge base data in TOON format from assets folder
- Robot speaks response using Android TextToSpeech API
- Real-time interruption detection: if user speaks while robot is talking, immediately stop TTS and re-evaluate response

**Data Management:**
- Menu data stored in TOON format files in Android assets folder
- Knowledge base (restaurant info, policies, hours, etc.) stored in TOON format files in Android assets folder
- Files are hardcoded into the app (no dynamic loading, no backend)
- English language only for Phase 1

**Wake Word Detection:**
- Implement "Hey, Temi" wake word detection
- Continuous background listening for wake word (when robot is idle)
- Manual activation button available in settings screen as alternative

**Interruption Handling:**
- Monitor audio input while TTS is actively speaking
- Detect when user begins speaking during robot's response
- Immediately stop TTS output
- Clear conversation state and reevaluate response based on interruption
- Update status indicator from green (speaking) to red (listening/thinking)

**Visual Status Indicator:**
- Fullscreen pulsating circle implemented with Jetpack Compose animations
- Three states with distinct visual appearance:
  - Idle: Grey static circle
  - Listening/Thinking: Red pulsating circle (breathing animation)
  - Speaking: Green pulsating circle (breathing animation)
- Smooth transitions between states

**Question Answering:**
- Robot can answer questions about menu items (ingredients, prices, descriptions, allergens)
- Robot can answer questions about restaurant information from knowledge base
- LLM-powered natural language understanding for varied question formats
- Context-aware responses using TOON-formatted data

### Technical Architecture

**Android Application Stack:**
- **Platform:** Android Native (Temi Robot OS)
- **Language:** Kotlin
- **Build Tool:** Gradle
- **UI Framework:** Jetpack Compose
- **Architecture Pattern:** Clean Architecture with MVVM
- **Concurrency:** Kotlin Coroutines
- **Reactive Streams:** Kotlin Flow
- **Dependency Injection:** Koin
- **Robot Integration:** Temi SDK

**AI/ML Components:**
- **Local LLM:** Gemma 3n-E2B (2GB footprint, mobile-optimized, multimodal)
- **LLM Framework:** Google AI Edge SDK
- **Speech-to-Text:** Google on-device Speech Recognition API (android.speech.SpeechRecognizer)
- **Text-to-Speech:** Android TextToSpeech API with English language pack
- **Wake Word Detection:** Custom implementation or Porcupine/Snowboy (TBD during spec writing)

**Data Layer:**
- **Format:** TOON (Token-Oriented Object Notation)
- **Storage Location:** Android assets folder (`assets/menu.toon`, `assets/knowledge-base.toon`)
- **Access Pattern:** Static file loading at app startup
- **No Database:** No Room, no SQLite, no external storage

**Key Integration Points:**
- Temi SDK for robot-specific features (navigation, display, hardware access)
- Google AI Edge SDK for Gemma 3n-E2B inference
- Android Speech Recognition for STT
- Android TextToSpeech for voice output
- Wake word detection library integration

### Reusability Opportunities

No similar existing features identified in the codebase. This is a new implementation with no code reuse opportunities at this time.

**Potential Future Reuse:**
- Visual status indicator component (pulsating circle) could be reused in future features
- TOON data loading utilities could be extracted for other local data needs
- Wake word detection service could be reused for other voice-activated features
- Interruption handling logic could inform future conversational features

### Scope Boundaries

**In Scope for Phase 1:**
- Android app with Temi SDK integration
- Wake word detection ("Hey, Temi") with manual button fallback
- Local speech-to-text using Google Speech Recognition API
- Local LLM inference using Gemma 3n-E2B via Google AI Edge SDK
- Local text-to-speech using Android TTS API
- Fullscreen pulsating circle status indicator (grey/red/green states)
- Real-time interruption detection and handling
- Menu and knowledge base data in TOON format stored in assets folder
- Question answering about menu items and restaurant information
- English language only
- Automated tests for critical scenarios (wake word, LLM responses, interruption handling)
- Clean Architecture with MVVM pattern
- Jetpack Compose UI implementation

**Out of Scope for Phase 1:**
- Backend API or server infrastructure
- Database (PostgreSQL, Room, or any persistent storage)
- User authentication or authorization
- Multi-restaurant support
- Cloud LLM integration or fallback (no OpenAI GPT-4o)
- Robot-to-restaurant assignment system
- Role-based permissions (Admin, Manager, Staff roles)
- Analytics tracking or monitoring
- Business intelligence features
- Admin dashboard or web interface
- Multilingual support (Spanish, Portuguese) - English only
- Dynamic menu updates (data is hardcoded in assets)
- Network connectivity or online features
- User management or multi-tenancy
- Restaurant management CRUD operations

**Deferred to Later Phases:**
- Cloud LLM hybrid architecture (Phase 2+)
- Multilingual support for Spanish and Portuguese (Phase 2+)
- Backend integration and API connectivity (Phase 2+)
- Multi-restaurant platform features (Phase 3+)
- Admin dashboard and management tools (Phase 3+)
- Analytics and business intelligence (Phase 4+)

### Critical Test Scenarios for Automation

**Wake Word Detection Tests:**
- Verify "Hey, Temi" triggers conversation activation
- Verify false positive rate is acceptably low
- Verify manual button in settings also activates conversation
- Verify background listening doesn't drain excessive battery

**Speech Recognition Tests:**
- Verify accurate transcription of common menu queries
- Verify handling of unclear or garbled speech
- Verify timeout behavior when user doesn't speak after activation

**LLM Response Quality Tests:**
- Verify accurate answers to menu item questions (ingredients, prices)
- Verify accurate answers to knowledge base questions (hours, policies)
- Verify appropriate responses to out-of-scope questions
- Verify TOON format data is correctly loaded and accessible to LLM

**Interruption Handling Tests:**
- Verify TTS stops immediately when user speaks during robot's response
- Verify status indicator transitions from green to red on interruption
- Verify conversation state is properly reset after interruption
- Verify reevaluated response incorporates interruption context

**Status Indicator Tests:**
- Verify grey static circle displays when idle
- Verify red pulsating circle displays during listening/thinking
- Verify green pulsating circle displays during speaking
- Verify smooth transitions between states
- Verify fullscreen display is properly rendered

**Integration Tests:**
- Verify end-to-end conversation flow: wake word → listen → LLM → speak
- Verify end-to-end interruption flow: speaking → interrupt → stop → re-evaluate
- Verify Temi SDK integration doesn't conflict with core functionality
- Verify app performs within memory constraints (~2-3GB total)

### Technical Considerations

**Memory Management:**
- Gemma 3n-E2B requires ~2GB RAM
- Total app should stay within 2.5-3GB to ensure stability on Temi hardware
- Monitor memory usage during LLM inference and optimize if needed
- Consider lazy loading of TOON data if memory becomes constrained

**Performance Requirements:**
- Wake word detection: <100ms latency from speech to activation
- STT transcription: <2 seconds for typical user utterance
- LLM inference: <3 seconds for typical menu question response generation
- TTS output: Natural speaking pace (~150-180 words per minute)
- Interruption detection: <200ms from user speech to TTS stop

**Audio Processing Constraints:**
- Must handle simultaneous audio monitoring (wake word + interruption detection)
- Audio pipeline should not conflict with Temi SDK audio features
- Background listening for wake word should be CPU-efficient
- Interruption detection requires real-time audio level monitoring while TTS plays

**TOON Format Implementation:**
- Parse TOON files from assets folder at app startup
- Cache parsed data in memory for LLM prompt construction
- Validate TOON syntax and provide clear errors if malformed
- Structure data for efficient LLM context inclusion (minimize tokens)

**Error Handling:**
- Graceful fallback if wake word detection fails (manual button always available)
- Clear user feedback if STT fails to transcribe (retry prompt)
- Timeout handling for LLM inference (fallback to "I'm not sure" response)
- TTS failure handling (display text response as fallback)
- Memory pressure handling (release non-critical resources if needed)

**Accessibility Considerations:**
- Voice-first interaction naturally accessible for visually impaired users
- Manual button in settings provides alternative activation method
- Visual status indicator provides feedback for hearing-impaired users (though primary UX is voice)
- Consider vibration feedback for state transitions (Temi robot capabilities permitting)

**Testing Strategy:**
- Unit tests for individual components (wake word detector, TOON parser, interruption handler)
- Integration tests for conversation flow and interruption scenarios
- UI tests for status indicator state transitions using Compose UI Testing
- Mock Gemma 3n-E2B responses for faster test execution
- Use MockK for Kotlin component mocking
- Target test coverage for critical paths only (per standards: test core user flows, defer edge cases)

**Code Organization:**
- Clean Architecture layers: Presentation (Compose UI + ViewModels), Domain (Use Cases), Data (Repositories)
- Separate modules for: wake word detection, STT service, LLM service, TTS service, interruption handling
- TOON data loading utility as shared infrastructure component
- Koin dependency injection for loose coupling and testability

**Development Constraints:**
- Adhere to standards defined in `/Users/aj/agent_os_workspace/temi_poc_v1/agent-os/standards/`
- Follow Android best practices for Jetpack Compose UI development
- Maintain clear separation of concerns per Clean Architecture principles
- Use Kotlin Coroutines for async operations (no blocking calls on main thread)
- Follow existing tech stack conventions (Kotlin, Gradle, Jetpack Compose, Koin)

**Known Technical Risks:**
- Gemma 3n-E2B performance on Temi hardware unknown (needs validation)
- Wake word detection accuracy in noisy restaurant environment (may need tuning)
- Real-time interruption detection complexity (audio pipeline conflicts)
- Memory footprint may exceed 2-3GB target (requires monitoring and optimization)
- TOON format is relatively new (community support may be limited)

**Mitigation Strategies:**
- Early performance testing of Gemma 3n-E2B on actual Temi hardware
- Configurable wake word sensitivity for environment tuning
- Careful audio pipeline architecture to isolate wake word, STT, and interruption detection
- Aggressive memory profiling during development with Android Profiler
- Fallback to JSON if TOON parsing proves problematic (though token efficiency will suffer)
