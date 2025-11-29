# Spec Requirements: PHASE 2 - Cloud Integration (Gemini Live API)

## Initial Description
Add cloud-based speech processing via Google Gemini Live API (2.5 Flash) for STT/TTS with fallback to local model. Implement hybrid switching logic based on connectivity. Ensure status indicator reflects processing state.

**PHASE:** This is Phase 2 of the project. Phase 1 (local PoC) must be completed first.

**NOTE:** Architecture evolved during requirements gathering - using Google Gemini Live API (2.5 Flash) for STT/TTS only, with separate LLM for conversational logic.

## Requirements Discussion

### First Round Questions

This section documents the initial architectural decisions that shaped the feature.

**Q1: Cloud LLM Selection**
**Question:** The original spec mentioned OpenAI GPT-4o, but I'm assuming we should consider Google Gemini Live API (2.5 Flash) instead, given its superior background noise filtering ability and native multilingual support (24+ languages including English, Spanish, Portuguese). Should we use Gemini Live API for the cloud integration?

**Answer:** YES - Use Google Gemini Live API (2.5 Flash), BUT with a critical clarification on its role.

**Q2: Integration Point**
**Question:** I'm thinking the API integration should happen directly from the Android app rather than routing through your Rails backend. This reduces latency and simplifies the architecture. Should we integrate directly from Android, or would you prefer a backend proxy?

**Answer:** Direct integration from Android app.

**Q3: Noise Cancellation Strategy**
**Question:** Given Gemini's built-in noise filtering, should we add additional noise cancellation/filtering on the Android side before sending audio to Gemini, or rely solely on Gemini's capabilities?

**Answer:** Add client-side noise cancellation using Android's built-in AudioEffect.NoiseSuppressor class.

### Critical Architectural Clarification

**IMPORTANT DISTINCTION:**

The feature architecture uses Gemini Live API for **audio processing only**, NOT for conversational intelligence:

**Gemini's Role (Audio Processing):**
- Speech-to-Text (STT): Convert user audio input to text
- Text-to-Speech (TTS): Convert response text to audio output
- Noise filtering for audio streams

**LLM's Role (Conversational Logic):**
- Local Gemma model (primary for POC)
- Cloud LLM (future option)
- Processes text input and generates response text
- Maintains conversation context and logic

**Architecture Flow:**
```
Audio Input
  -> Android Noise Cancellation (AudioEffect.NoiseSuppressor)
  -> Gemini STT
  -> Text
  -> LLM (local Gemma or cloud)
  -> Response Text
  -> Gemini TTS
  -> Audio Output
```

### Follow-up Questions

**Follow-up 1: API Key Security**
**Question:** Since this is a POC with direct Android-to-Gemini integration, how should we handle API key security? Options: (1) Accept the risk and document it, (2) Implement basic obfuscation, (3) Add backend proxy layer?

**Answer:** Accept the risk for POC and document as a known limitation for production.

**Follow-up 2: Noise Cancellation Implementation**
**Question:** For Android-side noise cancellation, should we use Android's built-in AudioEffect.NoiseSuppressor class, or integrate a third-party library like WebRTC's noise suppression?

**Answer:** Use Android's built-in AudioEffect.NoiseSuppressor class.

**Follow-up 3: Fallback Logic to Local Gemma**
**Question:** When Gemini API becomes unavailable (network loss, rate limits, errors), how should the fallback work? Should it: (1) Auto-detect and switch to local Gemma, (2) Prompt user to choose, (3) Queue requests for retry?

**Answer:** Implement comprehensive fallback with multiple layers:
- Auto-detect network loss and switch to local Gemma LLM
- Use same conversation context when switching between cloud/local LLM
- Show visual indicator for offline mode
- Retry Gemini STT/TTS periodically when connectivity returns
- Add settings option for "local-only mode" that forces use of local LLM and bypasses Gemini entirely (no cloud API calls)

**Follow-up 4: Audio Streaming vs Buffering**
**Question:** Should audio stream continuously to Gemini for real-time STT, or buffer and send in chunks?

**Answer:** Stream to Gemini for STT/TTS, BUT also keep local transcription for logging/debugging purposes.

**Follow-up 5: Status Indicator States**
**Question:** Beyond the existing states (red=thinking, green=speaking, grey=idle), do we need additional states like "connecting to cloud" or "offline mode"?

**Answer:** Keep default states only - no additional states needed.

**Follow-up 6: Testing Approach**
**Question:** How should we test the noisy environment scenarios? Options: (1) Audio file playback in emulator, (2) Physical device testing in actual restaurant, (3) Audio simulation tools?

**Answer:** Emulator-based testing initially with audio file playback.

**Follow-up 7: Restaurant Menu Integration**
**Question:** Should Gemini have access to the restaurant's menu data for context, or handle this separately?

**Answer:** Not relevant for this spec - Gemini only handles STT/TTS, not conversational logic. Menu context would be provided to the LLM layer, not Gemini.

**Follow-up 8: Cost and Rate Limiting**
**Question:** Given Gemini API costs, should we implement conversation time limits or rate limiting per device?

**Answer:** YES - Implement both conversation time limits and rate limiting per device.

### Existing Code to Reference

No similar existing features identified for reference. This is a new implementation.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
N/A - No visuals available for analysis.

## Requirements Summary

### Functional Requirements

**Core Audio Processing Pipeline:**
- Integrate Google Gemini Live API (2.5 Flash) for Speech-to-Text (STT) and Text-to-Speech (TTS) only
- Implement direct API calls from Android application to Gemini
- Add client-side noise cancellation using Android's AudioEffect.NoiseSuppressor before audio reaches Gemini
- Stream audio continuously to Gemini for real-time STT
- Maintain local transcription copy for logging and debugging

**LLM Conversational Logic:**
- Use local Gemma model as primary LLM for conversational intelligence
- LLM processes text (from Gemini STT) and generates response text
- LLM maintains conversation context and logic
- LLM has no direct audio processing responsibilities

**Three-Layer Fallback Architecture:**
1. **Primary Mode:** Gemini STT/TTS + Local Gemma LLM (default for POC)
2. **Network Failure Mode:** Fully local (no Gemini, local STT/TTS + local LLM)
3. **User Preference Mode:** "Local-only mode" setting that bypasses all cloud APIs

**Fallback Behavior:**
- Auto-detect network loss and gracefully degrade to local-only mode
- Preserve conversation context when switching between modes
- Show visual indicator for offline mode
- Retry Gemini STT/TTS connection periodically when connectivity returns
- Allow manual override via settings for "local-only mode"

**Status Indicator Integration:**
- Integrate with existing status indicator system
- Red pulsating = thinking/processing
- Green pulsating = speaking/responding
- Grey static = idle/ready
- No additional states for cloud/offline modes

**Multilingual Support:**
- Support English, Spanish, and Portuguese languages
- Leverage Gemini's 24+ language capabilities for STT/TTS

**Interruption Handling:**
- Gracefully handle user interruptions
- Stop current speech output
- Re-evaluate response based on new input

**Cost and Usage Management:**
- Implement conversation time limits per session
- Add rate limiting per device to control API usage
- Track API call metrics for cost monitoring

### Reusability Opportunities

No existing components or features identified for reuse. This is net-new functionality.

### Scope Boundaries

**In Scope:**
- Google Gemini Live API integration for STT/TTS only
- Direct Android-to-Gemini API communication
- Client-side noise cancellation using Android AudioEffect.NoiseSuppressor
- Local Gemma LLM integration for conversational logic (separate from Gemini)
- Three-layer fallback logic (Gemini STT/TTS + local LLM -> Fully local -> User-forced local-only)
- Auto-detection of network failures with graceful degradation
- Conversation context preservation across mode switches
- Status indicator updates during processing
- Local transcription logging for debugging
- Conversation time limits and rate limiting
- Settings option for "local-only mode"
- Visual indicator for offline mode
- Multilingual support (English, Spanish, Portuguese)
- Emulator-based testing with audio file playback

**Out of Scope:**
- Backend proxy for API calls (explicitly decided against for POC)
- Server-side audio processing
- Alternative cloud LLM providers
- Gemini as conversational LLM (only used for STT/TTS)
- Production-grade API key security (accepted risk for POC)
- Physical device testing in actual restaurant environments (emulator-based for POC)
- Advanced obfuscation or encryption for API keys
- Third-party noise cancellation libraries (using Android built-in)

### Technical Considerations

**Platform & Environment:**
- Platform: Android application running on Temi robot
- Environment: Loud restaurant settings with background noise and conversations
- Testing: Emulator-based with audio file playback initially

**Audio Processing Stack:**
- Client-side noise cancellation: Android AudioEffect.NoiseSuppressor
- STT: Google Gemini Live API 2.5 Flash (streaming)
- TTS: Google Gemini Live API 2.5 Flash
- Local backup: Local STT/TTS for offline mode
- Transcription logging: Local storage for debugging

**LLM Processing Stack:**
- Primary LLM: Local Gemma 2/3 model (on-device)
- Future option: Cloud LLM (not Gemini - separate service)
- Context management: Preserve across fallback transitions
- Input: Text from Gemini STT
- Output: Text to Gemini TTS

**Network & Connectivity:**
- Hybrid architecture: Prefers cloud (Gemini STT/TTS) but fully functional offline
- Auto-detection: Monitor network status and API availability
- Retry logic: Periodic reconnection attempts when connectivity returns
- Manual override: Settings toggle for "local-only mode"

**Security Considerations:**
- API Key Risk: ACCEPTED for POC - document as known limitation
- Storage: API keys stored on Android device (not secure for production)
- Future mitigation: Backend proxy or token service for production
- Documentation: Explicitly note security limitations in spec

**Cost Management:**
- Conversation time limits per session to control API usage
- Rate limiting per device to prevent runaway costs
- Metrics tracking for monitoring and optimization
- Local-only mode option to eliminate API costs when preferred

**Languages & Localization:**
- Primary languages: English, Spanish, Portuguese
- Gemini API: Supports 24+ languages natively
- LLM: Language support depends on Gemma model capabilities

**Integration Points:**
- Status indicator system (existing)
- Local Gemma model runtime (existing)
- Android audio system (AudioEffect, MediaRecorder)
- Gemini Live API (new external dependency)
- Settings/preferences system (for local-only mode toggle)

**Testing Strategy:**
- Initial testing: Android emulator
- Audio testing: Pre-recorded audio files with various noise levels
- Network testing: Simulate connectivity loss and restoration
- Fallback testing: Verify smooth transitions between modes
- Rate limit testing: Validate conversation time limits and device throttling
- No physical device testing in actual restaurants for POC phase

### Architecture Summary

**Complete Processing Flow:**

1. **Audio Input Stage:**
   - User speaks into Temi robot microphone
   - Android AudioEffect.NoiseSuppressor filters background noise
   - Clean audio stream ready for STT

2. **Speech Recognition Stage:**
   - Primary path: Stream to Gemini Live API for real-time STT
   - Fallback path: Local STT if network unavailable or local-only mode enabled
   - Output: Text transcription
   - Side effect: Store local copy for logging/debugging

3. **LLM Processing Stage:**
   - Input: Text from STT stage
   - Processing: Local Gemma model (or future cloud LLM) generates response
   - Context: Maintain conversation history across mode switches
   - Output: Response text

4. **Speech Synthesis Stage:**
   - Primary path: Send response text to Gemini Live API for TTS
   - Fallback path: Local TTS if network unavailable or local-only mode enabled
   - Output: Audio stream

5. **Audio Output Stage:**
   - Play synthesized speech through Temi robot speaker
   - Update status indicator (green pulsating during speech)

**Fallback Hierarchy:**

1. **Optimal Mode (Cloud Audio + Local LLM):**
   - Audio: Gemini STT/TTS
   - LLM: Local Gemma
   - Requires: Network connectivity
   - Quality: Best audio quality, good conversation quality

2. **Degraded Mode (Fully Local):**
   - Audio: Local STT/TTS
   - LLM: Local Gemma
   - Requires: Nothing (offline capable)
   - Quality: Reduced audio quality, same conversation quality
   - Trigger: Auto-detect network failure

3. **User-Forced Local Mode:**
   - Audio: Local STT/TTS (no Gemini calls)
   - LLM: Local Gemma
   - Requires: User setting enabled
   - Quality: Same as degraded mode
   - Trigger: User preference in settings

**State Management:**
- Track current mode (cloud audio, local-only, user-forced local)
- Monitor network connectivity continuously
- Preserve conversation context across transitions
- Display appropriate visual indicators
- Log mode transitions for debugging

### Known Limitations & Risks

**Security:**
- API keys stored on Android device without encryption (POC only)
- Direct API calls from client expose keys to potential extraction
- Mitigation: Document as production blocker, plan backend proxy for future

**Testing:**
- Emulator-based testing may not fully represent real restaurant noise
- Audio file playback doesn't test real-time microphone noise cancellation
- Mitigation: Plan physical device testing for production validation

**Cost:**
- Gemini API calls incur per-use costs
- Conversation time limits may frustrate users with complex orders
- Mitigation: Monitor usage, adjust limits based on real data

**Network Dependency:**
- Optimal experience requires stable network connection
- Fallback to local mode reduces audio quality
- Mitigation: Ensure local mode is fully functional and well-tested

**Language Support:**
- Gemini supports 24+ languages, but LLM may have limitations
- Language switching behavior undefined
- Mitigation: Document supported languages clearly, test language transitions
