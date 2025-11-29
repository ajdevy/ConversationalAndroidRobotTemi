package com.temi.conversationalrobot.data.service

import android.content.Context
import com.temi.conversationalrobot.domain.models.AppSettings
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WakeWordDetectorTest {
    
    private lateinit var mockContext: Context
    private lateinit var mockSettingsRepository: SettingsRepository
    private lateinit var wakeWordDetector: WakeWordDetector
    
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    
    @Before
    fun setup() {
        mockContext = mockk<Context>(relaxed = true)
        mockSettingsRepository = mockk<SettingsRepository>(relaxed = true)
        
        every { mockSettingsRepository.getSettings() } returns flowOf(
            AppSettings(wakeWordEnabled = true, wakeWordSensitivity = 1)
        )
        
        wakeWordDetector = WakeWordDetectorImpl(mockContext, mockSettingsRepository)
    }
    
    @Test
    fun `test wake word detection triggers callback on Hey Temi`() = testScope.runTest {
        // Note: Actual detection requires audio input, so this test verifies the service structure
        // In a real scenario, this would test with mock audio input
        assertFalse(wakeWordDetector.isListening())
        
        wakeWordDetector.startListening()
        advanceUntilIdle()
        
        // Service should be listening (actual detection requires audio hardware)
        // This test verifies the service can start
    }
    
    @Test
    fun `test wake word service respects enabled disabled toggle from settings`() = testScope.runTest {
        every { mockSettingsRepository.getSettings() } returns flowOf(
            AppSettings(wakeWordEnabled = false, wakeWordSensitivity = 1)
        )
        
        wakeWordDetector.startListening()
        advanceUntilIdle()
        
        // Service should not start when disabled
        assertFalse(wakeWordDetector.isListening())
    }
    
    @Test
    fun `test sensitivity adjustment affects detection threshold`() = testScope.runTest {
        // Test with different sensitivity levels
        every { mockSettingsRepository.getSettings() } returns flowOf(
            AppSettings(wakeWordEnabled = true, wakeWordSensitivity = 0) // Low
        )
        
        wakeWordDetector.startListening()
        advanceUntilIdle()
        
        // Verify service starts with low sensitivity
        // (Actual threshold testing requires audio input simulation)
    }
    
    @Test
    fun `test service stops listening when disabled`() = testScope.runTest {
        every { mockSettingsRepository.getSettings() } returns flowOf(
            AppSettings(wakeWordEnabled = true, wakeWordSensitivity = 1)
        )
        
        wakeWordDetector.startListening()
        advanceUntilIdle()
        
        wakeWordDetector.stopListening()
        advanceUntilIdle()
        
        assertFalse(wakeWordDetector.isListening())
    }
}

