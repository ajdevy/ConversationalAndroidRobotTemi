# Requirements: LLM Integration - Local

## Feature Overview

Integrate Gemma 3 local LLM model into the Temi Restaurant Assistant Android app for fully offline conversational capabilities. Implement comprehensive prompt engineering for restaurant Q&A scenarios with menu search integration. Update status indicator to red pulsating circle during LLM processing to provide clear visual feedback to users.

## Business Context

### Problem Statement
The current Temi Restaurant Assistant requires cloud connectivity for LLM functionality, which creates dependency on network availability and introduces latency. Restaurant environments may have unreliable WiFi, and customers expect instant responses even in offline scenarios. The hybrid architecture needs a robust local LLM implementation to ensure consistent service quality regardless of connectivity.

### Success Criteria
- Local LLM responds to restaurant Q&A queries with <2 second latency
- Menu search functionality operates entirely offline
- Status indicator accurately reflects LLM processing state (red pulsating during thinking)
- Battery impact remains under 1% for 25 conversations
- Model loads within 3 seconds at app startup
- Zero dependency on network connectivity for core conversational features
- Responses maintain natural conversational quality comparable to cloud models

### User Impact
**Restaurant Patrons:** Experience consistent, fast responses to menu and restaurant questions even when WiFi is unavailable or slow. Clear visual feedback (red pulsating status) shows when the robot is thinking.

**Restaurant Managers:** Confidence that the robot provides reliable service regardless of internet connectivity, reducing service disruptions and customer complaints.

**Restaurant Owners:** Reduced cloud API costs through local inference, improved service reliability, and better ROI on the robot investment.

## Functional Requirements

### Core Features

#### 1. Gemma 3 Model Integration
**Priority:** Critical
**Description:** Integrate Gemma 3 2B-4B or Gemma 3n mobile-optimized variant into the Android app using llama.cpp or MediaPipe GenAI API.

**Acceptance Criteria:**
- Model file (GGUF format or MediaPipe format) bundled with app or downloaded on first launch
- Model loads successfully on Temi robot hardware
- Memory footprint stays under 2GB
- Inference latency under 2 seconds for typical restaurant queries
- Model persists in memory to avoid reload delays

**Technical Requirements:**
- Use Gemma 3 2B-4B or Gemma 3n mobile-optimized variant
- Implementation via llama.cpp (C++ bindings) or MediaPipe GenAI API
- KV cache optimization leveraging Gemma 3's 60%→15% memory reduction
- Quantization support (4-bit or 8-bit) for optimal performance/quality balance
- Support for Android NDK integration if using llama.cpp

#### 2. Offline-First Architecture
**Priority:** Critical
**Description:** Local LLM must handle ALL restaurant Q&A scenarios independently without network dependency.

**Acceptance Criteria:**
- All conversational features work with airplane mode enabled
- No API calls required for LLM inference
- Menu search operates entirely on device
- Knowledge base queries resolved locally
- Graceful handling when model not yet loaded

**Technical Requirements:**
- Repository layer checks local model availability before attempting inference
- Domain layer enforces offline-first strategy
- No fallback to cloud API (this is the local implementation)
- Local data persistence using Room database for menu and knowledge base

#### 3. Menu Search Integration (Local)
**Priority:** Critical
**Description:** LLM performs semantic search and retrieval over restaurant menu data stored locally.

**Acceptance Criteria:**
- Menu data indexed and searchable offline
- LLM can answer questions about dishes, ingredients, prices, allergens
- Responses cite specific menu items when relevant
- Search handles natural language queries (e.g., "What vegetarian options do you have?")
- Multi-language support (English, Spanish, Portuguese)

**Technical Requirements:**
- Menu data retrieved from Room database
- Prompt engineering includes menu context in system prompt or user prompt
- Semantic search implementation (vector embeddings or keyword matching)
- Maximum menu context size: 4K tokens to fit within Gemma 3 context window
- Cache preprocessed menu embeddings for fast retrieval

#### 4. Prompt Engineering Framework
**Priority:** Critical
**Description:** Comprehensive prompt templates for restaurant Q&A scenarios optimized for Gemma 3.

**Acceptance Criteria:**
- System prompt defines assistant role, personality, and constraints
- Menu data injected into prompts efficiently
- Prompt templates for common queries: menu questions, ingredients, prices, wait times, hours, location
- Responses stay concise (2-3 sentences max for voice output)
- Hallucination mitigation: responses grounded in menu/knowledge base data
- Multi-turn conversation support with context retention

**Technical Requirements:**
- Prompt templates stored in Kotlin code or configuration files
- Dynamic menu context injection based on query relevance
- Context window management: prioritize recent messages + relevant menu items
- Response formatting optimized for TTS output
- Instruction-following format compatible with Gemma 3

**Example Prompt Structure:**
```
System: You are a helpful restaurant assistant. Answer customer questions about our menu, hours, and services. Keep responses brief (2-3 sentences). Only provide information from the menu below. If you don't know, say so.

Menu:
[Dynamically injected menu items]

User: {customer_query}
Assistant:
```

#### 5. Response Streaming
**Priority:** High
**Description:** Stream LLM responses token-by-token to enable progressive TTS and perceived responsiveness.

**Acceptance Criteria:**
- Tokens emitted as they are generated, not batched
- UI updates progressively as response streams
- TTS begins speaking before full response completes
- Stream cancellation supported for interruption handling
- Error handling for stream failures

**Technical Requirements:**
- Use Kotlin Flow for streaming responses from LLM layer to ViewModel
- ViewModel exposes StateFlow or SharedFlow for UI observation
- Integration with TTS pipeline for progressive speech output
- Cancellation support via CoroutineScope cancellation

#### 6. Status Indicator Integration
**Priority:** High
**Description:** Update visual status indicator to red pulsating circle during LLM processing, green pulsating during speech output, grey static when idle.

**Acceptance Criteria:**
- Red pulsating circle displays immediately when LLM inference starts
- Transitions to green pulsating when TTS begins speaking
- Returns to grey static when interaction completes
- Smooth transitions between states (no flickering)
- Indicator visible throughout conversation

**Technical Requirements:**
- ViewModel tracks LLM processing state: IDLE, THINKING, SPEAKING
- Compose UI observes state and renders appropriate indicator
- Pulsating animation implemented with Compose animations
- State transitions triggered by: LLM start, LLM completion, TTS start, TTS completion

#### 7. Model Loading Strategy
**Priority:** High
**Description:** Pre-load Gemma 3 model at app startup to minimize first-query latency.

**Acceptance Criteria:**
- Model loads during app initialization (splash screen or background)
- First user query receives response within 2 seconds (no cold start penalty)
- Loading progress visible to user during startup
- Fallback behavior if loading fails: show error message, retry option
- Model persists in memory until app termination

**Technical Requirements:**
- Model initialization in Application class or startup ViewModel
- Coroutine-based async loading with lifecycle awareness
- Model singleton or scoped dependency via Koin
- Error handling: catch load failures, log errors, notify user
- Memory management: unload model on low memory warnings

#### 8. Error Handling
**Priority:** Medium
**Description:** Display clear error messages when LLM fails (model not loaded, inference error, out of memory).

**Acceptance Criteria:**
- User sees friendly error message when LLM fails
- Error message explains issue: "AI model unavailable", "Processing error", etc.
- Option to retry query
- Errors logged for debugging
- Status indicator returns to idle/error state on failure

**Technical Requirements:**
- Try-catch blocks around inference calls
- Sealed class Result type: Success, Loading, Error
- ViewModel maps errors to user-friendly messages
- Logging via SLF4J/Logback for production debugging
- Error state displayed in UI (dialog or toast)

### API Requirements

#### API Integrations (For Data Retrieval Only)
**Note:** LLM inference and menu search are fully local. APIs are only used to fetch menu and knowledge base data for local storage.

**Menu Data API:**
- Endpoint: GET /api/restaurants/{restaurantId}/menus
- Purpose: Fetch menu items for local caching
- Frequency: On app startup, on manual refresh
- Data cached in Room database for offline access

**Knowledge Base API:**
- Endpoint: GET /api/restaurants/{restaurantId}/knowledge-base
- Purpose: Fetch restaurant-specific information (hours, location, policies)
- Frequency: On app startup, on manual refresh
- Data cached in Room database for offline access

### Performance Requirements

- **Model Load Time:** ≤3 seconds at app startup
- **First Token Latency:** ≤500ms after user query
- **Total Response Time:** ≤2 seconds for typical queries
- **Memory Usage:** ≤2GB for model + inference
- **Battery Impact:** <1% per 25 conversations
- **App Size Increase:** ≤500MB (for bundled model)
- **Streaming Latency:** Tokens emitted every 50-100ms

### Security & Privacy Requirements

- Model runs entirely on-device (no data leaves device during inference)
- No cloud API calls for LLM functionality (privacy-first)
- Menu data encrypted at rest in Room database
- User conversation history stored locally, cleared on app exit
- No telemetry or analytics for user queries (PoC only)

### Accessibility Requirements

- Status indicator colors meet WCAG contrast requirements
- Text alternatives for visual status (announcement via TTS)
- Support for large text sizes in error messages
- Screen reader compatibility for error dialogs

## Technical Architecture

### System Components

#### 1. Presentation Layer (Jetpack Compose)
**Component:** LLMStatusIndicator composable
- Displays red/green/grey pulsating or static circle
- Observes ViewModel state for transitions
- Smooth animation transitions

**Component:** ChatViewModel
- Manages conversation state
- Triggers LLM inference via use case
- Exposes StateFlow for UI observation
- Handles error states

#### 2. Domain Layer (Use Cases)
**Component:** ProcessUserQueryUseCase
- Accepts user input, returns Flow<LLMResponse>
- Orchestrates: menu search → prompt construction → LLM inference
- Error handling and retry logic

**Component:** LoadLLMModelUseCase
- Loads Gemma 3 model at startup
- Returns success/failure state
- Manages model lifecycle

#### 3. Data Layer (Repositories)
**Component:** LocalLLMRepository
- Wraps llama.cpp or MediaPipe GenAI API
- Handles inference requests
- Streams responses as Flow<String>
- Model lifecycle management (load, unload)

**Component:** MenuRepository
- Fetches menu data from API (network layer)
- Caches in Room database
- Provides offline access to menu items
- Exposes Flow<List<MenuItem>> for reactive updates

**Component:** KnowledgeBaseRepository
- Fetches knowledge base from API
- Caches in Room database
- Provides offline access to restaurant info

#### 4. LLM Integration Layer
**Option A: llama.cpp via JNI**
- C++ library compiled for Android ARM64
- JNI bindings to Kotlin
- GGUF model format
- Manual memory management

**Option B: MediaPipe GenAI API**
- High-level Kotlin/Java API
- Simplified model loading and inference
- Built-in quantization support
- Better Android integration

**Recommendation:** Start with MediaPipe GenAI API for faster development, fallback to llama.cpp if more control needed.

### Data Models

```kotlin
// Domain Models
data class LLMResponse(
    val text: String,
    val isComplete: Boolean,
    val error: String? = null
)

sealed class LLMState {
    object Idle : LLMState()
    object Loading : LLMState()
    object Thinking : LLMState()
    object Speaking : LLMState()
    data class Error(val message: String) : LLMState()
}

data class UserQuery(
    val text: String,
    val language: String,
    val conversationHistory: List<Message>
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val allergens: List<String>
)

data class KnowledgeBase(
    val restaurantId: String,
    val hours: String,
    val location: String,
    val policies: String
)
```

### Integration Points

#### Existing Systems
**Temi SDK:**
- Speech-to-text input feeds into LLM
- TTS output speaks LLM responses
- Robot state (navigation, idle) informs when to activate LLM

**Room Database:**
- Menu data cached locally
- Knowledge base cached locally
- Conversation history stored (optional)

**Retrofit API:**
- Fetches menu and knowledge base data for caching
- No LLM API calls

#### New Components
**LLM Inference Engine:**
- Gemma 3 model loaded via MediaPipe or llama.cpp
- Inference triggered by user queries
- Streams responses to ViewModel

**Prompt Engineering Module:**
- Constructs prompts with menu context
- Manages context window size
- Multi-turn conversation history

### Dependencies

**New Gradle Dependencies:**
```kotlin
// MediaPipe GenAI (Option A)
implementation("com.google.mediapipe:tasks-genai:0.10.0")

// OR llama.cpp (Option B)
implementation("com.github.username:llama-cpp-android:version")

// Existing Dependencies (already in project)
implementation("androidx.room:room-runtime:2.5.2")
implementation("androidx.room:room-ktx:2.5.2")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("io.insert-koin:koin-android:3.4.0")
implementation("com.squareup.retrofit2:retrofit:2.9.0")
```

**Model Files:**
- Gemma 3 2B Q4 GGUF: ~1.5GB (download or bundle)
- Gemma 3 4B Q4 GGUF: ~2.5GB (download or bundle)
- Gemma 3n mobile variant: ~1GB (optimized for mobile)

### File Structure

```
app/src/main/kotlin/com/temi/restaurant/
├── data/
│   ├── llm/
│   │   ├── LocalLLMRepositoryImpl.kt       # Gemma 3 inference
│   │   ├── LLMModelManager.kt              # Model lifecycle
│   │   └── PromptTemplate.kt               # Prompt construction
│   ├── menu/
│   │   ├── MenuRepositoryImpl.kt           # Room + API
│   │   └── MenuDao.kt                      # Room DAO
│   └── knowledge/
│       ├── KnowledgeBaseRepositoryImpl.kt
│       └── KnowledgeBaseDao.kt
├── domain/
│   ├── usecases/
│   │   ├── ProcessUserQueryUseCase.kt
│   │   ├── LoadLLMModelUseCase.kt
│   │   └── SearchMenuUseCase.kt
│   ├── repositories/
│   │   ├── LocalLLMRepository.kt           # Interface
│   │   ├── MenuRepository.kt
│   │   └── KnowledgeBaseRepository.kt
│   └── models/
│       ├── LLMResponse.kt
│       ├── LLMState.kt
│       └── UserQuery.kt
└── presentation/
    ├── chat/
    │   ├── ChatViewModel.kt
    │   ├── ChatScreen.kt
    │   └── LLMStatusIndicator.kt           # Red/green/grey circle
    └── di/
        └── LLMModule.kt                    # Koin DI module
```

## Testing Requirements

### Unit Tests (JUnit 4 + MockK)

**LocalLLMRepository Tests:**
- Mock llama.cpp/MediaPipe API
- Test inference response streaming
- Test error handling (model not loaded, OOM)
- Test prompt construction
- Target: 90%+ coverage

**ProcessUserQueryUseCase Tests:**
- Mock menu repository and LLM repository
- Test query flow: input → menu search → inference → output
- Test error propagation
- Test conversation history handling

**ChatViewModel Tests:**
- Mock use cases
- Test state transitions: Idle → Thinking → Speaking → Idle
- Test error state handling
- Use kotlinx-coroutines-test for coroutine testing
- Use Turbine for Flow testing

### Integration Tests

**LLM + Menu Search Integration:**
- Use real menu data from Room (in-memory database)
- Mock LLM responses for predictable testing
- Verify prompt includes relevant menu items

**Status Indicator Integration:**
- Verify red circle appears during LLM processing
- Verify green circle during TTS
- Verify smooth transitions

### UI Tests (Kaspresso + Compose Testing)

**ChatScreen Tests:**
- User types query → status turns red → response appears → status returns to grey
- Error message displayed on LLM failure
- Streaming response updates UI progressively

### Performance Tests

- Measure model load time on Temi hardware
- Measure inference latency for sample queries
- Monitor memory usage during inference
- Test battery drain over 25 conversations

### Manual Testing Scenarios

**Scenario 1: Offline Menu Query**
1. Enable airplane mode
2. Ask "What vegetarian options do you have?"
3. Verify: Red status during processing, response includes menu items, green status during speech

**Scenario 2: Model Load on Startup**
1. Launch app (cold start)
2. Observe loading indicator
3. Verify: Model loads within 3 seconds, first query responds quickly

**Scenario 3: Error Handling**
1. Simulate model load failure (corrupt file)
2. Attempt user query
3. Verify: Error message displayed, option to retry

**Scenario 4: Multi-turn Conversation**
1. Ask "What's on the menu?"
2. Follow up "Does it have gluten?"
3. Verify: Context retained, response references previous query

## User Experience Requirements

### Visual Design
- **Status Indicator Size:** 80dp diameter circle, centered on screen
- **Colors:**
  - Red: #FF3B30 (thinking)
  - Green: #34C759 (speaking)
  - Grey: #8E8E93 (idle)
- **Animation:** Pulsating scale from 1.0 to 1.15 over 800ms, repeat infinitely
- **Idle State:** Static circle (no animation)

### Interaction Flow
1. User speaks query (via Temi microphone)
2. STT converts to text
3. Status indicator turns red (pulsating)
4. LLM processes query (streaming)
5. First tokens trigger TTS start
6. Status indicator turns green (pulsating)
7. TTS speaks response
8. Status returns to grey (static)

### Error States
- **Model Not Loaded:** "The AI assistant is not ready. Please wait a moment and try again."
- **Inference Error:** "Sorry, I couldn't process that. Please try asking again."
- **Out of Memory:** "The AI assistant encountered a problem. Please restart the app."

### Loading States
- **App Startup:** "Loading AI assistant..." (progress bar optional)
- **First Query (if model still loading):** "AI assistant is starting up, one moment..."

## Migration & Deployment

### Rollout Strategy
This is a new feature (not a migration). Deploy as part of app update.

**Phase 1: Internal Testing**
- Deploy to test devices (Temi robots)
- Validate model performance, latency, battery impact
- Test offline scenarios extensively

**Phase 2: Pilot Deployment**
- Deploy to 1-2 pilot restaurant locations
- Monitor for errors, performance issues
- Gather feedback on response quality

**Phase 3: Full Rollout**
- Deploy to all Temi robots via app update
- Include model file in APK or download on first launch
- Monitor crash rates, performance metrics

### Deployment Configuration
**Model Bundling Options:**
- Option A: Bundle model in APK (increases APK size by ~1.5-2.5GB)
- Option B: Download model on first launch (requires initial network, longer first-run time)
- **Recommendation:** Option B (download on first launch) to keep APK manageable

**Feature Flags:**
- `enable_local_llm`: Toggle local LLM on/off (fallback to cloud if disabled)
- `preload_model_on_startup`: Toggle model preloading behavior

### Backward Compatibility
- Not applicable (new feature)
- Ensure cloud LLM fallback still works if local model fails

## Open Questions & Decisions Needed

### Resolved Decisions
- **Model Selection:** Gemma 3 2B-4B or Gemma 3n (mobile-optimized)
- **Offline Strategy:** Fully offline, no cloud fallback
- **Menu Search:** Local search only
- **Status Indicator:** Integrated in this task
- **Model Loading:** Pre-load at startup
- **Streaming:** Enabled
- **Error Handling:** Show error message

### Remaining Questions
None at this time. All clarifying questions have been answered.

## Success Metrics

### Primary Metrics
- **Response Latency:** <2 seconds for 90% of queries
- **Model Load Time:** <3 seconds at app startup
- **Battery Impact:** <1% per 25 conversations
- **Offline Success Rate:** 100% for cached menu queries
- **Error Rate:** <1% of queries fail

### Secondary Metrics
- **User Satisfaction:** Measured via pilot feedback
- **Response Quality:** Manual review of 50+ sample responses
- **Memory Usage:** Stays under 2GB during inference
- **App Size:** APK + model under 3GB total

### Monitoring
- Log LLM inference times to analytics
- Track error rates by error type
- Monitor memory usage patterns
- Collect user feedback during pilot

## Related Documentation

- **Product Mission:** `/Users/aj/agent-os/product/mission.md`
- **Tech Stack:** `/Users/aj/agent-os/product/tech-stack.md`
- **Deployment Setup:** `/Users/aj/agent-os/product/deployment-setup.md`
- **Gemma 3 Documentation:** https://ai.google.dev/gemma
- **MediaPipe GenAI API:** https://developers.google.com/mediapipe/solutions/genai
- **llama.cpp:** https://github.com/ggerganov/llama.cpp

## Appendix

### Gemma 3 Model Variants

**Gemma 3 2B:**
- Parameters: 2 billion
- Quantized Size (Q4): ~1.5GB
- Best for: Fast inference, lower memory
- Tradeoff: Slightly lower quality responses

**Gemma 3 4B:**
- Parameters: 4 billion
- Quantized Size (Q4): ~2.5GB
- Best for: Higher quality responses
- Tradeoff: Slower inference, more memory

**Gemma 3n (mobile-optimized):**
- Parameters: ~2B (optimized architecture)
- Quantized Size: ~1GB
- Best for: Mobile deployment, battery efficiency
- Tradeoff: Specialized for on-device, may need testing

**Recommendation:** Start with Gemma 3 2B Q4 for best balance of speed and quality. Upgrade to 4B if quality is insufficient. Consider Gemma 3n if battery/memory is a constraint.

### Prompt Engineering Best Practices

**Keep Prompts Concise:**
- Gemma 3 has 8K context window, but shorter is faster
- Include only relevant menu items (filter by category if possible)
- Prioritize recent conversation history (last 3-5 turns)

**Ground Responses in Data:**
- Explicitly instruct: "Only use information from the menu below"
- Include fallback instruction: "If you don't know, say 'I don't have that information'"

**Optimize for Voice Output:**
- Instruct: "Keep responses brief, 2-3 sentences max"
- Avoid lists: "Instead of listing items, describe a few highlights"

**Multi-turn Context:**
- Include last 3-5 user/assistant messages for context
- Use clear role labels: "User:", "Assistant:"

### Example Prompts

**Simple Menu Query:**
```
System: You are a helpful restaurant assistant. Answer questions about our menu. Keep responses brief.

Menu:
- Caesar Salad ($12): Romaine lettuce, parmesan, croutons, Caesar dressing
- Margherita Pizza ($18): Tomato sauce, mozzarella, basil
- Grilled Salmon ($28): Atlantic salmon, lemon butter, vegetables

User: What vegetarian options do you have?
Assistant:
```

**Multi-turn Query:**
```
System: You are a helpful restaurant assistant. Answer questions about our menu. Keep responses brief.

Menu: [...]

User: Tell me about your pizzas.
Assistant: We have a Margherita Pizza with tomato sauce, mozzarella, and basil for $18, and a Pepperoni Pizza for $20.

User: Does the Margherita have gluten?
Assistant:
```

### Resources

- **Gemma 3 Model Card:** https://ai.google.dev/gemma/docs/model_card
- **Android NDK Guide:** https://developer.android.com/ndk
- **Jetpack Compose Animations:** https://developer.android.com/jetpack/compose/animation
- **Kotlin Flow:** https://kotlinlang.org/docs/flow.html
- **Koin Dependency Injection:** https://insert-koin.io/
