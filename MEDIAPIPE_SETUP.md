# MediaPipe Tasks GenAI Setup for Gemma 3n-E2B

## Quick Start Guide

### Step 1: Update Dependencies

Update `data/build.gradle.kts`:

```kotlin
dependencies {
    // Remove deprecated SDK
    // implementation("com.google.ai.edge:generativeai:0.2.0")
    
    // Add MediaPipe Tasks GenAI
    implementation("com.google.mediapipe:tasks-genai:0.10.24")
}
```

### Step 2: Update AndroidManifest.xml

Add native library support for hardware acceleration (Android 12+):

```xml
<manifest>
    <!-- ... existing permissions ... -->
    
    <!-- Add these for GPU acceleration support -->
    <uses-native-library 
        android:name="libOpenCL.so" 
        android:required="false"/>
    <uses-native-library 
        android:name="libOpenCL-car.so" 
        android:required="false"/>
    <uses-native-library 
        android:name="libOpenCL-pixel.so" 
        android:required="false"/>
    
    <!-- ... rest of manifest ... -->
</manifest>
```

### Step 3: Update LlmService Implementation

The `LlmService.kt` needs to be updated to use MediaPipe's LLM Inference API. See the updated implementation file.

### Step 4: Model Configuration

MediaPipe will automatically:
- Download Gemma 3n-E2B model on first use
- Store model in app's internal storage (~2-3GB)
- Cache model for offline use after first download

**Requirements:**
- Internet connection for initial model download
- ~3GB free storage space
- Android API level 24+ (already configured)

### Step 5: Test on Physical Device

MediaPipe LLM Inference works best on:
- High-end Android devices
- Physical devices (not emulators)
- Devices with sufficient RAM (4GB+ recommended)

## API Usage Example

```kotlin
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions

// Initialize
val options = LlmInferenceOptions.builder()
    .setModelName("gemma-3n-2b") // or "gemma-2b"
    .setDelegate(LlmInferenceOptions.Delegate.GPU) // or CPU
    .build()

val llmInference = LlmInference.createFromOptions(context, options)

// Generate response
val response = llmInference.generateResponse(prompt)
```

## Troubleshooting

1. **Model Download Fails**: Check internet connection and storage space
2. **Out of Memory**: Reduce model size or use CPU delegate
3. **Slow Inference**: Use GPU delegate if available
4. **Model Not Found**: Verify model name matches MediaPipe's supported models

## References

- MediaPipe GenAI: https://ai.google.dev/edge/mediapipe/solutions/genai
- LLM Inference API: https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference


