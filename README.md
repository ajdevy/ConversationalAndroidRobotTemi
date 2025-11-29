# Temi POC Workspace

This workspace contains the Temi conversational robot proof-of-concept project.

## Structure

- **`root`** - Android application implementation
  - Full Android project with Clean Architecture
  - Local LLM (Gemma 3n-E2B) integration
  - STT, TTS, and wake word detection
  - See `README.md` for details

- **`agent-os/`** - Agent OS framework and specifications
  - Project specifications
  - Task definitions
  - Planning documents
  - Agent profiles and workflows

## Quick Start

### Android App

Navigate to the Android project:

```bash
cd temi-poc-android
```

Then follow the instructions in `temi-poc-android/QUICK_START.md` or `temi-poc-android/README.md`.

### Specifications

View project specifications in:

```
agent-os/specs/2025-11-17-phase-1-local-poc/
```

## Documentation

- **Android App**: See `temi-poc-android/README.md`
- **Specifications**: See `agent-os/specs/`
- **Testing**: See `temi-poc-android/TESTING_CHECKLIST.md`
