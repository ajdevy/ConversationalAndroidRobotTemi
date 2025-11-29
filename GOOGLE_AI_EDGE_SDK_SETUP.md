# Google AI Edge SDK Configuration Guide for Gemma 3n-E2B

## Recommended Approach: MediaPipe Tasks GenAI

**Current Status**: Google recommends using **MediaPipe Tasks GenAI** with LLM Inference API for on-device Gemma 3n-E2B integration on Android.

## Option 1: MediaPipe Tasks GenAI (Recommended)

This is the official Google-recommended approach for running Gemma 3n-E2B on-device.

### Setup Steps for MediaPipe Tasks GenAI:

1. **Update Dependencies** in `data/build.gradle.kts`:
```kotlin
dependencies {
    // MediaPipe Tasks GenAI for LLM Inference
    implementation("com.google.mediapipe:tasks-genai:0.10.24")
    
    // Optional: Function Calling support
    // implementation("com.google.ai.edge.localagents:localagents-fc:0.1.0")
}
```

2. **Update AndroidManifest.xml** to add native library support (for Android 12+):
```xml
<!-- Add inside <application> tag or as separate tags -->
<uses-native-library android:name="libOpenCL.so" android:required="false"/>
<uses-native-library android:name="libOpenCL-car.so" android:required="false"/>
<uses-native-library android:name="libOpenCL-pixel.so" android:required="false"/>
```

3. **Update LlmService** to use MediaPipe LLM Inference API (see implementation below).

4. **Model Download**: MediaPipe will automatically download Gemma 3n-E2B model on first use (requires internet connection initially).

## Option 2: Use TensorFlow Lite with Gemma Model

If you specifically need Gemma 3n-E2B, you can use TensorFlow Lite:

### Setup Steps:

1. **Add TensorFlow Lite Dependencies** in `data/build.gradle.kts`:
```kotlin
dependencies {
    // TensorFlow Lite for on-device inference
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0") // Optional: GPU acceleration
    
    // TensorFlow Lite Support Library
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
}
```

2. **Download Gemma 3n-E2B Model**:
   - Download from Hugging Face: https://huggingface.co/google/gemma-3n-2b
   - Convert to TensorFlow Lite format if needed
   - Place model file in `app/src/main/assets/models/gemma_3n_2b.tflite`

3. **Update LlmService** to use TensorFlow Lite interpreter.

## Option 3: Use MediaPipe LLM Inference (If Available)

MediaPipe may provide LLM inference capabilities:

### Setup Steps:

1. **Add MediaPipe Dependencies**:
```kotlin
dependencies {
    // Check for MediaPipe LLM support
    // implementation("com.google.mediapipe:mediapipe:+")
}
```

## Option 4: Continue with Deprecated SDK (Not Recommended)

If you must use the deprecated `com.google.ai.edge:generativeai:0.2.0` SDK:

### Setup Steps:

1. **Model Files Location**:
   - The SDK should automatically download model files on first use
   - Model files are typically stored in app's internal storage
   - Ensure sufficient storage space (~2-3GB for Gemma 3n-E2B)

2. **Permissions** (already in AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.INTERNET" />
<!-- Needed for initial model download -->
```

3. **Storage Configuration**:
   - Ensure device has at least 3GB free storage
   - Model will be downloaded on first initialization

4. **Model Initialization**:
   - The current implementation in `LlmService.kt` handles initialization
   - Model is loaded at app startup in `ConversationalRobotApplication`

5. **Model Name**:
   - Current implementation uses: `"gemma-3n-2b"`
   - Alternative names to try:
     - `"gemma-2b"`
     - `"gemma-3n-e2b"`
     - Check SDK documentation for exact model identifier

## Recommended Approach: Update to ML Kit Prompt API

Since Google AI Edge SDK is deprecated, here's how to migrate:

### Step 1: Update Dependencies

Replace in `data/build.gradle.kts`:
```kotlin
// Remove deprecated SDK
// implementation("com.google.ai.edge:generativeai:0.2.0")

// Add ML Kit Prompt API
implementation("com.google.mlkit:prompt-api:1.0.0")
```

### Step 2: Update LlmService Implementation

The service would need to be updated to use ML Kit Prompt API's `PromptModel` and `PromptClient` instead of `GenerativeModel`.

## Troubleshooting

### If Model Fails to Load:

1. **Check Storage**: Ensure device has sufficient storage (3GB+)
2. **Check Internet**: First-time download requires internet connection
3. **Check Logs**: Look for initialization errors in logcat
4. **Verify Model Name**: Ensure model identifier matches SDK expectations
5. **Check SDK Version**: Verify compatibility with Android API level 24+

### Alternative: Use Cloud API with Local Fallback

If on-device model proves problematic, consider:
- Using Google's Gemini API with local caching
- Implementing hybrid approach (cloud with offline fallback)

## Next Steps

1. **Choose an approach** based on your requirements
2. **Update dependencies** accordingly
3. **Modify LlmService** to use chosen SDK/API
4. **Test model loading** on target device
5. **Monitor memory usage** (target: <3GB total)

## References

- ML Kit Prompt API: https://developer.android.com/ai/gemini-nano/ai-edge-sdk
- TensorFlow Lite: https://www.tensorflow.org/lite
- Gemma Models: https://huggingface.co/google/gemma-3n-2b

