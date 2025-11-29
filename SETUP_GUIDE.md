# Quick Setup Guide: Google AI Edge SDK / MediaPipe for Gemma 3n-E2B

## ✅ Already Configured

The project has been updated with the recommended MediaPipe Tasks GenAI setup:

1. ✅ **Dependencies Updated**: `data/build.gradle.kts` now includes MediaPipe Tasks GenAI
2. ✅ **AndroidManifest Updated**: Native library support added for GPU acceleration
3. ✅ **Implementation Ready**: `LlmService.kt` uses reflection-based initialization

## 📋 Configuration Steps

### Step 1: Sync Gradle Dependencies

Run in Android Studio or terminal:
```bash
./gradlew build --refresh-dependencies
```

### Step 2: Verify Dependencies

Check that `com.google.mediapipe:tasks-genai:0.10.24` is resolved in `data/build.gradle.kts`.

### Step 3: First Run Setup

When you first run the app:

1. **Internet Connection Required**: MediaPipe will download Gemma 3n-E2B model (~2-3GB)
2. **Storage Space**: Ensure device has at least 3GB free storage
3. **Model Location**: Model will be cached in app's internal storage after first download

### Step 4: Test Model Initialization

The model initializes automatically at app startup in `ConversationalRobotApplication.onCreate()`.

Check logcat for:
- ✅ Success: "Model initialized successfully"
- ❌ Errors: Check error messages in `LlmResult.Error`

## 🔧 Current Implementation

The `LlmService` implementation uses reflection to handle API differences:

- **Model Name**: `"gemma-3n-2b"` (E2B variant)
- **Initialization**: Automatic at app startup
- **Memory**: ~2GB for model + ~1GB for app = ~3GB total
- **Timeout**: 10 seconds for inference

## 🚀 Switching to MediaPipe Implementation

If you want to use the MediaPipe-specific implementation:

1. **Update DataModule.kt**:
```kotlin
// Change from:
single<LlmService> { LlmServiceImpl(androidContext<Context>(), get()) }

// To:
single<LlmService> { LlmServiceMediaPipeImpl(androidContext<Context>(), get()) }
```

2. The MediaPipe implementation (`LlmServiceMediaPipe.kt`) is already created and ready to use.

## 📱 Testing on Device

### Requirements:
- **Physical Device**: MediaPipe works best on physical devices (not emulators)
- **Android API**: 24+ (already configured)
- **RAM**: 4GB+ recommended for smooth operation
- **Storage**: 3GB+ free space

### First Launch:
1. Connect device via USB or WiFi ADB
2. Install app: `./gradlew installDebug`
3. Launch app - model will download automatically
4. Wait for model download to complete (may take several minutes)
5. Test conversation flow

## 🐛 Troubleshooting

### Model Download Fails
- **Check**: Internet connection
- **Check**: Storage space (3GB+)
- **Check**: Logcat for specific error messages

### Out of Memory
- **Solution**: Use CPU delegate instead of GPU
- **Solution**: Reduce model size if available
- **Check**: Device has sufficient RAM (4GB+)

### Model Not Found
- **Check**: Model name matches MediaPipe's supported models
- **Try**: `"gemma-2b"` instead of `"gemma-3n-2b"`
- **Check**: MediaPipe SDK version compatibility

### Slow Inference
- **Solution**: Use GPU delegate if device supports it
- **Note**: First inference is slower (model loading)
- **Check**: Device meets minimum requirements

## 📚 Additional Resources

- **MediaPipe GenAI Docs**: https://ai.google.dev/edge/mediapipe/solutions/genai
- **LLM Inference API**: https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference
- **Gemma Models**: https://huggingface.co/google/gemma-3n-2b

## 🔄 Alternative: Use Deprecated SDK

If MediaPipe doesn't work, you can fall back to the deprecated SDK:

1. **Uncomment** in `data/build.gradle.kts`:
```kotlin
implementation("com.google.ai.edge:generativeai:0.2.0")
```

2. **Comment out** MediaPipe:
```kotlin
// implementation("com.google.mediapipe:tasks-genai:0.10.24")
```

3. The current `LlmServiceImpl` will work with the deprecated SDK using reflection.

## ✅ Verification Checklist

- [ ] Gradle sync completed successfully
- [ ] MediaPipe dependency resolved
- [ ] AndroidManifest has native library declarations
- [ ] Device has 3GB+ free storage
- [ ] Internet connection available (for first run)
- [ ] App launches without crashes
- [ ] Model initializes (check logcat)
- [ ] Conversation flow works end-to-end


