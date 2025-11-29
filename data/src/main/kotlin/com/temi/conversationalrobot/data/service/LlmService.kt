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

class LlmServiceImpl(
    private val context: Context,
    private val toonRepository: ToonRepository
) : LlmService {
    
    private val _responses = MutableSharedFlow<LlmResult>(replay = 0)
    override val responses: Flow<LlmResult> = _responses.asSharedFlow()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val promptBuilder = PromptBuilder()
    private var generativeModel: Any? = null // GenerativeModel from Google AI Edge SDK
    private var isInitialized = false
    
    companion object {
        private const val TIMEOUT_MS = 10000L // 10 seconds
        // Gemma 3n-E2B model identifier
        private const val MODEL_NAME = "gemma-3n-2b" // E2B variant (2B effective parameters)
    }
    
    override suspend fun initialize() {
        if (isInitialized) return
        
        try {
            // Try MediaPipe Tasks GenAI first (recommended)
            val mediaPipeModel = tryInitializeMediaPipe()
            if (mediaPipeModel != null) {
                generativeModel = mediaPipeModel
                isInitialized = true
                android.util.Log.d("LlmService", "Initialized Gemma 3n-E2B using MediaPipe Tasks GenAI")
                return
            }
            
            // Fallback to deprecated Google AI Edge SDK
            val modelClass = try {
                Class.forName("com.google.ai.edge.generativeai.GenerativeModel")
            } catch (e: ClassNotFoundException) {
                _responses.emit(LlmResult.Error("Neither MediaPipe nor Google AI Edge SDK found. Please add com.google.mediapipe:tasks-genai dependency."))
                return
            }
            
            // Try different constructor signatures
            val model = try {
                // Try: GenerativeModel(Context, String)
                val constructor1 = modelClass.getConstructor(Context::class.java, String::class.java)
                constructor1.newInstance(context, MODEL_NAME)
            } catch (e: Exception) {
                try {
                    // Try: GenerativeModel(String, Context)
                    val constructor2 = modelClass.getConstructor(String::class.java, Context::class.java)
                    constructor2.newInstance(MODEL_NAME, context)
                } catch (e2: Exception) {
                    try {
                        // Try with ModelName enum
                        val modelNameClass = Class.forName("com.google.ai.edge.generativeai.ModelName")
                        val modelNameField = try {
                            modelNameClass.getField("GEMMA_3N_2B")
                        } catch (e3: NoSuchFieldException) {
                            try {
                                modelNameClass.getField("GEMMA_2B")
                            } catch (e4: NoSuchFieldException) {
                                null
                            }
                        }
                        val modelName = modelNameField?.get(null)
                        if (modelName != null) {
                            val constructor3 = modelClass.getConstructor(modelNameClass, Context::class.java)
                            constructor3.newInstance(modelName, context)
                        } else {
                            throw Exception("Could not find ModelName enum value")
                        }
                    } catch (e3: Exception) {
                        throw Exception("Failed to initialize model: ${e.message}")
                    }
                }
            }
            
            generativeModel = model
            isInitialized = true
            android.util.Log.d("LlmService", "Initialized Gemma 3n-E2B using Google AI Edge SDK (deprecated)")
        } catch (e: Exception) {
            android.util.Log.e("LlmService", "Failed to initialize model: ${e.message}", e)
            _responses.emit(LlmResult.Error("Failed to initialize Gemma 3n-E2B model: ${e.message}"))
        }
    }
    
    private suspend fun tryInitializeMediaPipe(): Any? {
        return try {
            val llmInferenceClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference")
            val optionsClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions")
            val builderClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions\$Builder")
            
            val builder = builderClass.getMethod("builder").invoke(null)
            val setModelNameMethod = builderClass.getMethod("setModelName", String::class.java)
            setModelNameMethod.invoke(builder, MODEL_NAME)
            
            val delegateClass = try {
                Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions\$Delegate")
            } catch (e: ClassNotFoundException) {
                null
            }
            
            if (delegateClass != null) {
                val cpuDelegate = delegateClass.getField("CPU").get(null)
                val setDelegateMethod = builderClass.getMethod("setDelegate", delegateClass)
                setDelegateMethod.invoke(builder, cpuDelegate)
            }
            
            val options = builderClass.getMethod("build").invoke(builder)
            val createMethod = llmInferenceClass.getMethod("createFromOptions", Context::class.java, optionsClass)
            createMethod.invoke(null, context, options)
        } catch (e: Exception) {
            android.util.Log.d("LlmService", "MediaPipe initialization failed, trying fallback: ${e.message}")
            null
        }
    }
    
    override suspend fun generateResponse(userUtterance: String) {
        if (!isInitialized) {
            initialize()
        }
        
        val model = generativeModel ?: run {
            _responses.emit(LlmResult.Error("Model not initialized"))
            return
        }
        
        try {
            val menuData = toonRepository.getMenuData().first().getOrNull()
            val kbData = toonRepository.getKnowledgeBaseData().first().getOrNull()
            
            val prompt = promptBuilder.buildPrompt(userUtterance, menuData, kbData)
            
            withTimeout(TIMEOUT_MS) {
                android.util.Log.d("LlmService", "Generating response for: ${userUtterance.take(50)}...")
                
                // Generate response using Gemma 3n-E2B
                val response = try {
                    // Try MediaPipe generateResponse method first
                    try {
                        val method1 = model.javaClass.getMethod("generateResponse", String::class.java)
                        method1.invoke(model, prompt)
                    } catch (e: NoSuchMethodException) {
                        // Try generateContent(String) method (deprecated SDK)
                        try {
                            val method2 = model.javaClass.getMethod("generateContent", String::class.java)
                            method2.invoke(model, prompt)
                        } catch (e2: NoSuchMethodException) {
                            // Try generate(String) method
                            val method3 = model.javaClass.getMethod("generate", String::class.java)
                            method3.invoke(model, prompt)
                        }
                    }
                } catch (e: Exception) {
                    throw Exception("Could not find generate method: ${e.message}")
                }
                
                // Extract text from response
                val responseText = when {
                    response is String -> response
                    response != null -> {
                        val methodText = listOf("getText", "text").firstNotNullOfOrNull { methodName ->
                            try {
                                val method = response.javaClass.getMethod(methodName)
                                method.invoke(response) as? String
                            } catch (e: Exception) {
                                null
                            }
                        }
                        
                        methodText ?: runCatching {
                            response.javaClass.getField("text").get(response) as? String
                        }.getOrNull() ?: response.toString()
                    }
                    else -> "I'm not sure how to answer that. Could you rephrase?"
                }
                
                _responses.emit(LlmResult.Success(responseText))
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            _responses.emit(LlmResult.Timeout)
        } catch (e: OutOfMemoryError) {
            // Handle memory pressure
            _responses.emit(LlmResult.Error("Memory limit exceeded. Please try a simpler question."))
        } catch (e: Exception) {
            _responses.emit(LlmResult.Error("Failed to generate response: ${e.message}"))
        }
    }
}

class PromptBuilder {
    fun buildPrompt(
        userUtterance: String,
        menuData: com.temi.conversationalrobot.data.models.MenuData?,
        kbData: com.temi.conversationalrobot.data.models.KnowledgeBaseData?
    ): String {
        val systemPrompt = "You are a helpful restaurant assistant. Answer questions about our menu and restaurant information based ONLY on the provided data. If you don't know the answer, politely say so."
        
        val menuContext = menuData?.items?.joinToString("\n") { item ->
            "${item.name}: ${item.description}, Price: $${item.price}, Ingredients: ${item.ingredients}, Allergens: ${item.allergens}"
        } ?: ""
        
        val kbContext = kbData?.let { kb ->
            "Restaurant: ${kb.restaurantInfo.name}, Location: ${kb.restaurantInfo.location}, Phone: ${kb.restaurantInfo.phone}\n" +
            kb.faq.joinToString("\n") { faq -> "Q: ${faq.question} A: ${faq.answer}" }
        } ?: ""
        
        return "$systemPrompt\n\nMenu:\n$menuContext\n\nRestaurant Info:\n$kbContext\n\nUser Question: $userUtterance\n\nAnswer:"
    }
}

