# Quick Start Guide - Emulator Testing

## 🚀 Ready to Test!

The app is configured and ready for emulator testing. Follow these steps:

## Step 1: Open in Android Studio

1. Open Android Studio
2. File → Open → Select `/Users/aj/agent_os_workspace/temi_poc_v1`
3. Wait for Gradle sync to complete

## Step 2: Verify Emulator

1. Ensure emulator is running (or start one)
2. Emulator should have:
   - Android API 24+ (minimum SDK requirement)
   - At least 3GB free storage
   - Internet connection enabled

## Step 3: Build and Run

1. Select your emulator from device dropdown
2. Click **Run** button (▶️) or press `Shift+F10`
3. Wait for build and installation

## Step 4: Verify Launch

**Expected on Launch:**
- ✅ Black fullscreen background
- ✅ Grey static circle in center
- ✅ Settings button (FAB) in bottom-right corner
- ✅ No crashes

## Step 5: Test Basic Features

### Test Settings Screen
1. Tap Settings FAB
2. Verify all controls display correctly
3. Change a setting (e.g., TTS Speech Rate)
4. Go back and return - verify setting persisted

### Test Manual Activation
1. In Settings, tap "Start Conversation"
2. Status should change to red pulsating circle
3. Wait 5 seconds - should timeout and return to idle

## Step 6: Monitor Logcat

**Open Logcat in Android Studio and filter by:**
```
tag:LlmService
```

**Look for:**
- Model initialization messages
- Any error messages
- Service lifecycle logs

## Expected Behavior

### ✅ Should Work on Emulator:
- App launch
- UI rendering
- Settings screen
- Settings persistence
- Status indicator display
- Manual activation button
- Status state transitions

### ⚠️ May Not Work on Emulator:
- Wake word detection (microphone limitations)
- STT (microphone limitations)
- Model inference (may be very slow)
- GPU acceleration

### 📝 Model Initialization Notes:
- First run requires internet connection
- Model download (~2-3GB) may take several minutes
- Check Logcat for download progress
- After first download, model is cached locally

## Troubleshooting

### App Won't Build
- Check Gradle sync completed
- Verify all dependencies resolved
- Check for compilation errors in Build Output

### App Crashes on Launch
- Check Logcat for stack trace
- Verify Koin initialization
- Check AndroidManifest for errors

### Model Fails to Initialize
- Check internet connection
- Verify emulator storage (3GB+)
- Check Logcat for specific error
- Model download may take time

### Settings Don't Persist
- Check DataStore permissions
- Verify SettingsRepository registered in Koin
- Check Logcat for DataStore errors

## Quick Verification Commands

**Check if app is installed:**
```bash
adb shell pm list packages | grep conversationalrobot
```

**View app logs:**
```bash
adb logcat | grep -E "LlmService|ConversationalRobot|SettingsRepository"
```

**Clear app data (for fresh start):**
```bash
adb shell pm clear com.temi.conversationalrobot
```

## What's Next?

After verifying basic functionality on emulator:

1. **Test on Physical Device** - For full functionality including wake word, STT, and model inference
2. **Profile Performance** - Use Android Profiler to monitor memory and CPU
3. **Run Tests** - Execute unit and integration tests
4. **Optimize** - Based on performance data and testing results

## Support Files

- **EMULATOR_TESTING.md** - Detailed testing guide
- **TESTING_CHECKLIST.md** - Comprehensive checklist
- **SETUP_GUIDE.md** - Setup and configuration details
- **GOOGLE_AI_EDGE_SDK_SETUP.md** - SDK configuration guide


