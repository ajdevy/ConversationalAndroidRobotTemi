# Emulator Testing Guide

## Pre-Flight Checklist

Before testing on emulator, ensure:

- [ ] Android Studio is open with emulator running
- [ ] Emulator has Android API 24+ (minimum SDK requirement)
- [ ] Emulator has sufficient storage (3GB+ for model download)
- [ ] Emulator has internet connection (for first-time model download)
- [ ] Gradle sync completed successfully

## Quick Start Testing

### Step 1: Build the Project

In Android Studio:
1. Click **File → Sync Project with Gradle Files**
2. Wait for sync to complete
3. Check for any dependency resolution errors

### Step 2: Run on Emulator

1. Select your emulator from device dropdown
2. Click **Run** (green play button) or press `Shift+F10`
3. Wait for app to install and launch

### Step 3: Verify App Launch

Expected behavior:
- ✅ App launches with black fullscreen background
- ✅ Grey static circle appears in center (Idle state)
- ✅ Settings FAB button visible in bottom-right corner
- ✅ No crashes on startup

### Step 4: Test Settings Screen

1. Tap the Settings FAB (bottom-right)
2. Verify settings screen displays with:
   - "Start Conversation" button at top
   - Activation section (Wake Word toggle, Sensitivity slider)
   - Speech section (TTS Speech Rate slider)
   - Display section (Status Indicator Size slider)
   - Debug section (Debug Info toggle, Interruption Detection toggle)

### Step 5: Test Manual Activation

1. Tap "Start Conversation" button
2. Expected: Status indicator changes to red pulsating circle (Listening state)
3. Note: STT will require microphone permission - grant if prompted

### Step 6: Monitor Logcat

Watch for these log messages:

**Successful Initialization:**
```
LlmService: Initialized Gemma 3n-E2B using MediaPipe Tasks GenAI
```

**Model Download (First Run):**
```
MediaPipe: Downloading model gemma-3n-2b...
MediaPipe: Model download complete
```

**Error Messages:**
```
LlmService: Failed to initialize model: [error message]
```

## Expected Behavior by Feature

### Status Indicator
- **Idle**: Grey static circle (no animation)
- **Listening**: Red pulsating circle (breathing animation)
- **Thinking**: Red pulsating circle (same as listening)
- **Speaking**: Green pulsating circle (breathing animation)

### Settings Persistence
- Change any setting
- Close app completely
- Reopen app
- Verify setting persisted

### Manual Activation Flow
1. Tap "Start Conversation"
2. Status → Red (Listening)
3. Speak or wait for timeout
4. Status → Red (Thinking) - if speech detected
5. Status → Green (Speaking) - when LLM responds
6. Status → Grey (Idle) - when TTS completes

## Troubleshooting

### App Crashes on Launch

**Check:**
1. Logcat for stack traces
2. Verify all dependencies resolved
3. Check AndroidManifest for errors
4. Verify minimum SDK compatibility

**Common Issues:**
- Missing Koin initialization → Check Application class
- Missing permissions → Check AndroidManifest
- Module dependency issues → Verify settings.gradle.kts

### Model Fails to Initialize

**Check Logcat for:**
```
LlmService: Failed to initialize Gemma 3n-E2B model: [error]
```

**Possible Causes:**
1. MediaPipe dependency not resolved → Sync Gradle
2. Insufficient storage → Free up space on emulator
3. Network issues → Check internet connection
4. Model name incorrect → Check MODEL_NAME constant

**Solutions:**
- Try alternative model name: `"gemma-2b"` instead of `"gemma-3n-2b"`
- Check emulator storage: Settings → Storage
- Verify internet: Open browser on emulator

### Settings Don't Persist

**Check:**
1. DataStore permissions
2. Logcat for DataStore errors
3. Verify SettingsRepository is registered in Koin

### UI Not Rendering

**Check:**
1. Compose dependencies resolved
2. Material3 theme applied
3. ViewModel injection working
4. Logcat for Compose errors

## Performance Notes for Emulator

⚠️ **Important**: MediaPipe LLM Inference works best on physical devices. Emulator limitations:

- **Slower Inference**: Emulator may be significantly slower than physical device
- **Memory Constraints**: Emulator may have limited RAM
- **GPU Acceleration**: May not work on emulator (CPU fallback used)
- **Model Download**: May take longer on emulator

## Testing Checklist

### Basic Functionality
- [ ] App launches without crashes
- [ ] Status indicator displays correctly
- [ ] Settings screen accessible
- [ ] Settings persist after app restart
- [ ] Manual activation button works

### Conversation Flow (if model initializes)
- [ ] Manual activation triggers listening state
- [ ] STT captures speech (if permission granted)
- [ ] LLM generates response (may be slow on emulator)
- [ ] TTS speaks response (if TTS available)
- [ ] Status transitions work correctly

### Error Handling
- [ ] Model initialization errors handled gracefully
- [ ] STT timeout handled (5 seconds)
- [ ] LLM timeout handled (10 seconds)
- [ ] Error messages displayed to user

## Next Steps After Emulator Testing

1. **Test on Physical Device**: For accurate performance testing
2. **Verify Model Download**: Check internal storage for model files
3. **Test Wake Word**: Requires physical device with microphone
4. **Performance Profiling**: Use Android Profiler for memory/CPU
5. **Integration Testing**: Run full conversation flows

## Logcat Filters

Use these filters in Android Studio Logcat:

```
tag:LlmService
tag:ConversationalRobot
tag:SettingsRepository
tag:WakeWordDetector
tag:SttService
tag:TtsService
```

## Common Log Messages

**Success:**
```
LlmService: Initialized Gemma 3n-E2B using MediaPipe Tasks GenAI
LlmService: Generating response for: [user question]
```

**Errors:**
```
LlmService: Failed to initialize Gemma 3n-E2B model: [error]
LlmService: Model not initialized
```

**Warnings:**
```
LlmService: MediaPipe initialization failed, trying fallback
```


