# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep application class
-keep class com.temi.conversationalrobot.ConversationalRobotApplication { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep Temi SDK
-keep class com.robotemi.** { *; }

# Keep kotlin-toon
-keep class br.com.vexpera.toon.** { *; }

# Keep Google AI Edge SDK
-keep class com.google.ai.edge.** { *; }

# Keep Porcupine
-keep class ai.picovoice.porcupine.** { *; }

