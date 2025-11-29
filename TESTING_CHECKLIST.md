# Testing Checklist for Emulator

## Pre-Testing Setup

### ✅ Verify Project Structure
- [x] All modules created (app, presentation, domain, data)
- [x] Dependencies configured in build.gradle.kts files
- [x] AndroidManifest.xml configured
- [x] Koin DI modules registered
- [x] Application class created

### ✅ Verify Dependencies
- [x] MediaPipe Tasks GenAI dependency added
- [x] Compose dependencies added
- [x] Koin dependencies added
- [x] Coroutines dependencies added
- [x] Testing dependencies added

### ✅ Verify Implementation
- [x] TOON data files created (menu.toon, knowledge-base.toon)
- [x] All services implemented (WakeWord, STT, LLM, TTS, Interruption)
- [x] Use cases implemented
- [x] UI components implemented (StatusIndicator, Settings)
- [x] Navigation implemented

## Testing Steps

### 1. Build Verification

**In Android Studio:**
1. Open project
2. File → Sync Project with Gradle Files
3. Wait for sync to complete
4. Check Build Output for errors

**Expected:** No compilation errors

### 2. App Launch Test

**Steps:**
1. Select emulator from device dropdown
2. Click Run (▶️) or press Shift+F10
3. Wait for app to install and launch

**Expected Results:**
- ✅ App installs without errors
- ✅ App launches successfully
- ✅ Black fullscreen background displays
- ✅ Grey static circle in center (Idle state)
- ✅ Settings FAB visible in bottom-right

**If App Crashes:**
- Check Logcat for stack trace
- Verify Koin initialization
- Check for missing dependencies

### 3. Settings Screen Test

**Steps:**
1. Tap Settings FAB (bottom-right)
2. Verify settings screen displays

**Expected Results:**
- ✅ Settings screen opens
- ✅ "Start Conversation" button visible at top
- ✅ All sections display:
  - Activation (Wake Word toggle, Sensitivity slider)
  - Speech (TTS Speech Rate slider)
  - Display (Status Indicator Size slider)
  - Debug (Debug Info toggle, Interruption Detection toggle)

### 4. Settings Persistence Test

**Steps:**
1. Change any setting (e.g., TTS Speech Rate)
2. Navigate back to status indicator
3. Navigate back to settings
4. Verify setting persisted

**Expected:** Setting value matches what you set

### 5. Manual Activation Test

**Steps:**
1. Go to Settings screen
2. Tap "Start Conversation" button
3. Observe status indicator

**Expected Results:**
- ✅ Button becomes disabled
- ✅ Status indicator changes to red pulsating circle (Listening)
- ✅ After timeout (5s) or speech, transitions to Thinking/Speaking

**Note:** STT requires microphone permission - grant if prompted

### 6. Model Initialization Test

**Check Logcat for:**
```
tag:LlmService
```

**Expected Messages:**
- ✅ "Initialized Gemma 3n-E2B using MediaPipe Tasks GenAI" (success)
- OR "MediaPipe initialization failed, trying fallback" (fallback)
- OR "Failed to initialize Gemma 3n-E2B model: [error]" (failure)

**If Model Fails to Initialize:**
- Check internet connection (needed for first-time download)
- Verify emulator has 3GB+ free storage
- Check Logcat for specific error message
- Model download may take several minutes on first run

### 7. Status Indicator States Test

**Test Each State:**

**Idle:**
- ✅ Grey static circle (no animation)
- ✅ No color transitions

**Listening:**
- ✅ Red pulsating circle
- ✅ Breathing animation (scale 1.0 to 1.15)
- ✅ Smooth animation

**Thinking:**
- ✅ Red pulsating circle (same as Listening)
- ✅ Smooth animation

**Speaking:**
- ✅ Green pulsating circle
- ✅ Breathing animation
- ✅ Smooth color transition from red

### 8. Settings Controls Test

**Test Each Control:**

**Wake Word Toggle:**
- ✅ Toggle on/off works
- ✅ Setting persists

**Wake Word Sensitivity:**
- ✅ Slider moves (0, 1, 2)
- ✅ Label updates (Low/Medium/High)
- ✅ Setting persists

**TTS Speech Rate:**
- ✅ Slider moves (50-250 range)
- ✅ Value displays correctly
- ✅ Setting persists

**Status Indicator Size:**
- ✅ Slider moves (0.4-0.8 range)
- ✅ Percentage displays correctly
- ✅ Circle size changes (may need to return to status screen)
- ✅ Setting persists

**Debug Info Toggle:**
- ✅ Toggle on/off works
- ✅ Debug overlay appears/disappears on status screen
- ✅ Setting persists

**Interruption Detection Toggle:**
- ✅ Toggle on/off works
- ✅ Setting persists

### 9. Error Handling Test

**Test Error Scenarios:**

**STT Timeout:**
- ✅ After 5 seconds of silence, status returns to Idle
- ✅ Error message displayed (if implemented)

**LLM Timeout:**
- ✅ After 10 seconds, error state shown
- ✅ Error message displayed

**Model Not Initialized:**
- ✅ App still launches
- ✅ UI still functional
- ✅ Error message in Logcat

### 10. Navigation Test

**Test Navigation Flow:**
1. Status Indicator → Settings (via FAB)
2. Settings → Status Indicator (via back button)
3. Repeat multiple times

**Expected:**
- ✅ Smooth transitions
- ✅ No crashes
- ✅ State preserved

## Performance Checks

### Memory Usage
- Monitor with Android Profiler
- Target: <3GB total memory
- Check for memory leaks

### CPU Usage
- Monitor during idle state
- Should be minimal when idle
- Higher during model inference (expected)

### Battery Impact
- Monitor wake word background listening
- Should be CPU-efficient

## Known Limitations on Emulator

⚠️ **Emulator Constraints:**
- Model inference may be very slow
- GPU acceleration may not work
- Wake word detection may not work (microphone limitations)
- STT may not work (microphone limitations)
- Model download may take longer

✅ **What Should Work:**
- UI rendering
- Settings persistence
- Status indicator animations
- Navigation
- Manual activation button
- Settings controls

## Success Criteria

### Minimum Viable Test (Emulator)
- [x] App launches without crashes
- [x] Status indicator displays correctly
- [x] Settings screen accessible
- [x] Settings persist
- [x] Manual activation button works
- [x] Status transitions work (even if no actual speech/LLM)

### Full Functionality Test (Physical Device)
- [ ] Model initializes successfully
- [ ] Wake word detection works
- [ ] STT captures speech
- [ ] LLM generates responses
- [ ] TTS speaks responses
- [ ] Interruption detection works
- [ ] End-to-end conversation flow works

## Next Steps After Emulator Testing

1. **Fix any crashes or errors** found during testing
2. **Test on physical device** for full functionality
3. **Profile performance** with Android Profiler
4. **Run integration tests** if implemented
5. **Optimize** based on performance data

## Logcat Monitoring

**Key Tags to Filter:**
```
tag:LlmService
tag:SttService
tag:TtsService
tag:WakeWordDetector
tag:SettingsRepository
tag:ConversationalRobot
```

**Success Indicators:**
- Model initialization success messages
- Service lifecycle messages
- State transition logs

**Error Indicators:**
- Exception stack traces
- Initialization failures
- Timeout messages
- Permission denials


