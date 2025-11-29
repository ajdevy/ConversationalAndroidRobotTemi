#!/bin/bash

# Script to run Android UI tests with device connectivity checks

echo "Checking device connectivity..."
adb devices

# Wait for device to be online
echo "Waiting for device to come online..."
adb wait-for-device

# Check if device is online
DEVICE_STATUS=$(adb devices | grep -v "List" | grep "device$" | wc -l)

if [ "$DEVICE_STATUS" -eq 0 ]; then
    echo "ERROR: No online devices found. Please ensure emulator is running and connected."
    exit 1
fi

echo "Device is online. Running tests..."
cd "$(dirname "$0")"
./gradlew :app:connectedDebugAndroidTest --continue

