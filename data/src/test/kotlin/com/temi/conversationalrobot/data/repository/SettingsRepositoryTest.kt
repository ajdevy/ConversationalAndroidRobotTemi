package com.temi.conversationalrobot.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.temi.conversationalrobot.domain.models.AppSettings
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {
    
    private lateinit var testContext: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var settingsRepository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    
    @Before
    fun setup() {
        val tempDir = File.createTempFile("test_datastore", "")
        tempDir.delete()
        tempDir.mkdirs()
        
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope.backgroundScope,
            produceFile = { File(tempDir, "settings.preferences_pb") }
        )
        
        testContext = mockk(relaxed = true)
        settingsRepository = SettingsRepositoryImpl(testContext, dataStore)
    }
    
    @Test
    fun `test default settings values on first launch`() = testScope.runTest {
        advanceUntilIdle()
        val settings = settingsRepository.getSettings().first()
        
        assertEquals(true, settings.wakeWordEnabled)
        assertEquals(1, settings.wakeWordSensitivity)
        assertEquals(150f, settings.ttsSpeechRate)
        assertEquals(false, settings.debugInfoEnabled)
        assertEquals(0.6f, settings.statusIndicatorSize)
        assertEquals(true, settings.interruptionDetectionEnabled)
    }
    
    @Test
    fun `test settings persistence across repository recreation`() = testScope.runTest {
        settingsRepository.updateWakeWordEnabled(false)
        settingsRepository.updateTtsSpeechRate(200f)
        advanceUntilIdle()
        
        val settings1 = settingsRepository.getSettings().first()
        assertFalse(settings1.wakeWordEnabled)
        assertEquals(200f, settings1.ttsSpeechRate)
        
        // Create new repository instance
        val newRepository = SettingsRepositoryImpl(testContext, dataStore)
        advanceUntilIdle()
        val settings2 = newRepository.getSettings().first()
        
        assertFalse(settings2.wakeWordEnabled)
        assertEquals(200f, settings2.ttsSpeechRate)
    }
    
    @Test
    fun `test settings update and retrieval`() = testScope.runTest {
        settingsRepository.updateWakeWordSensitivity(2)
        settingsRepository.updateStatusIndicatorSize(0.8f)
        settingsRepository.updateDebugInfo(true)
        advanceUntilIdle()
        
        val settings = settingsRepository.getSettings().first()
        
        assertEquals(2, settings.wakeWordSensitivity)
        assertEquals(0.8f, settings.statusIndicatorSize)
        assertTrue(settings.debugInfoEnabled)
    }
    
    @Test
    fun `test all setting types Boolean Float Int`() = testScope.runTest {
        // Test Boolean
        settingsRepository.updateWakeWordEnabled(false)
        settingsRepository.updateInterruptionDetection(false)
        advanceUntilIdle()
        var settings = settingsRepository.getSettings().first()
        assertFalse(settings.wakeWordEnabled)
        assertFalse(settings.interruptionDetectionEnabled)
        
        // Test Int
        settingsRepository.updateWakeWordSensitivity(0)
        advanceUntilIdle()
        settings = settingsRepository.getSettings().first()
        assertEquals(0, settings.wakeWordSensitivity)
        
        // Test Float
        settingsRepository.updateTtsSpeechRate(100f)
        settingsRepository.updateStatusIndicatorSize(0.5f)
        advanceUntilIdle()
        settings = settingsRepository.getSettings().first()
        assertEquals(100f, settings.ttsSpeechRate)
        assertEquals(0.5f, settings.statusIndicatorSize)
    }
}

