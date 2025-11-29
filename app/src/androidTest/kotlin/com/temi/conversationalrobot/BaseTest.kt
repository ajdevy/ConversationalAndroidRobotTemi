package com.temi.conversationalrobot

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.temi.conversationalrobot.utils.testModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

abstract class BaseTest : KoinTest {
    
    protected val context: Context = ApplicationProvider.getApplicationContext()
    
    companion object {
        /**
         * Helper function to initialize Koin with test modules.
         * Call this from @BeforeClass in each test class.
         */
        @JvmStatic
        fun setupKoinForTests() {
            // Stop any existing Koin instance (from previous tests or production app)
            try {
                stopKoin()
            } catch (e: Exception) {
                // Ignore if Koin wasn't started
            }
            
            // Get the test application context
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
            
            // Initialize Koin with test modules
            // This runs before any test rules are created, so MainActivity will have Koin available
            startKoin {
                androidContext(appContext)
                modules(testModules)
            }
        }
        
        /**
         * Helper function to tear down Koin after tests.
         * Call this from @AfterClass in each test class.
         */
        @JvmStatic
        fun tearDownKoinForTests() {
            // Stop Koin after all tests in the class complete
            try {
                stopKoin()
            } catch (e: Exception) {
                // Ignore if Koin wasn't started
            }
        }
    }
    
    // Helper function to create test steps (simulating Kaspresso's step function)
    protected inline fun step(description: String, crossinline action: () -> Unit) {
        action()
    }
    
    // Helper function to create test body (simulating Kaspresso's run function)
    protected inline fun test(crossinline body: () -> Unit) {
        body()
    }
}

