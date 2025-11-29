package com.temi.conversationalrobot.data.service

import android.content.Context
import com.temi.conversationalrobot.data.repository.ToonRepository
import com.temi.conversationalrobot.domain.services.LlmResult
import com.temi.conversationalrobot.domain.services.LlmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

/**
 * Alternative implementation using MediaPipe Tasks GenAI LLM Inference API
 * This is the recommended approach for Gemma 3n-E2B on Android
 * 
 * To use this implementation:
 * 1. Update data/build.gradle.kts to use MediaPipe dependency
 * 2. Replace LlmServiceImpl with LlmServiceMediaPipeImpl in DataModule.kt
 */
class LlmServiceMediaPipeImpl(
    private val context: Context,
    private val toonRepository: ToonRepository
) : LlmService {
    
    private val _responses = MutableSharedFlow<LlmResult>(replay = 0)
    override val responses: Flow<LlmResult> = _responses.asSharedFlow()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val promptBuilder = PromptBuilder()
    private var llmInference: Any? = null // LlmInference from MediaPipe
    private var isInitialized = false
    
    companion object {
        private const val TIMEOUT_MS = 10000L // 10 seconds
        private const val MODEL_NAME = "gemma-3n-2b" // or "gemma-2b"
    }
    
    override suspend fun initialize() {
        if (isInitialized) return
        
        try {
            // Initialize MediaPipe LLM Inference
            val llmInferenceClass = try {
                Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference")
            } catch (e: ClassNotFoundException) {
                _responses.emit(LlmResult.Error("MediaPipe Tasks GenAI not found. Please add com.google.mediapipe:tasks-genai dependency."))
                return
            }
            
            val optionsClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions")
            val builderClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions\$Builder")
            
            // Create options builder
            val builder = builderClass.getMethod("builder").invoke(null)
            
            // Set model name
            val setModelNameMethod = builderClass.getMethod("setModelName", String::class.java)
            setModelNameMethod.invoke(builder, MODEL_NAME)
            
            // Set delegate (try GPU first, fallback to CPU)
            val delegateClass = try {
                Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions\$Delegate")
            } catch (e: ClassNotFoundException) {
                null
            }
            
            if (delegateClass != null) {
                val cpuDelegate = delegateClass.getField("CPU").get(null)
                val setDelegateMethod = builderClass.getMethod("setDelegate", delegateClass)
                setDelegateMethod.invoke(builder, cpuDelegate) // Use CPU for compatibility
            }
            
            // Build options
            val options = builderClass.getMethod("build").invoke(builder)
            
            // Create LlmInference instance
            val createMethod = llmInferenceClass.getMethod(
                "createFromOptions",
                Context::class.java,
                optionsClass
            )
            llmInference = createMethod.invoke(null, context, options)
            
            isInitialized = true
        } catch (e: Exception) {
            _responses.emit(LlmResult.Error("Failed to initialize MediaPipe LLM Inference: ${e.message}"))
        }
    }
    
    override suspend fun generateResponse(userUtterance: String) {
        if (!isInitialized) {
            initialize()
        }
        
        val inference = llmInference ?: run {
            _responses.emit(LlmResult.Error("LLM Inference not initialized"))
            return
        }
        
        try {
            val menuData = toonRepository.getMenuData().first().getOrNull()
            val kbData = toonRepository.getKnowledgeBaseData().first().getOrNull()
            
            val prompt = promptBuilder.buildPrompt(userUtterance, menuData, kbData)
            
            withTimeout(TIMEOUT_MS) {
                // Generate response using MediaPipe
                val generateMethod = try {
                    inference.javaClass.getMethod("generateResponse", String::class.java)
                } catch (e: NoSuchMethodException) {
                    inference.javaClass.getMethod("generate", String::class.java)
                }
                
                val response = generateMethod.invoke(inference, prompt)
                
                // Extract text from response
                val responseText = when {
                    response is String -> response
                    response != null -> {
                        val textMethod = try {
                            response.javaClass.getMethod("getText")
                        } catch (e: NoSuchMethodException) {
                            try {
                                response.javaClass.getMethod("text")
                            } catch (e2: NoSuchMethodException) {
                                null
                            }
                        }
                        textMethod?.invoke(response) as? String ?: response.toString()
                    }
                    else -> "I'm not sure how to answer that. Could you rephrase?"
                }
                
                _responses.emit(LlmResult.Success(responseText))
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            _responses.emit(LlmResult.Timeout)
        } catch (e: OutOfMemoryError) {
            _responses.emit(LlmResult.Error("Memory limit exceeded. Please try a simpler question."))
        } catch (e: Exception) {
            _responses.emit(LlmResult.Error("Failed to generate response: ${e.message}"))
        }
    }
}

