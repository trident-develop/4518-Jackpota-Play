# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JackpotaPlay ("Jackpota Play") is an Android slot machine game built with Jetpack Compose and Material Design 3. The project is in early-stage development with placeholder activity implementations.

- **Package/Namespace:** `com.gamehivecorp.taptita`
- **Min SDK:** 28 (Android 9.0) / **Compile & Target SDK:** 36 (Android 15)
- **Kotlin:** 2.0.21, **AGP:** 9.0.1, **Gradle:** 9.2.1

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew clean                  # Clean build artifacts
```

Run a single test class:
```bash
./gradlew test --tests "com.gamehivecorp.taptita.ExampleUnitTest"
```

## Architecture

- **LoadingActivity** — Launcher/entry point activity
- **MainActivity** — Secondary activity (not exported)
- Both use `ComponentActivity` with Jetpack Compose (`setContent`)
- **Theme system** in `ui/theme/` — Material 3 with dynamic color support (Android 12+), dark/light schemes
- Dependencies managed via version catalog at `gradle/libs.versions.toml`
- Single module (`:app`), no multi-module setup

## Key Tech Stack

- Jetpack Compose (BOM 2024.09.00) with Material 3
- AndroidX Lifecycle, Activity Compose
- JUnit 4 + AndroidX Test + Espresso for testing
- Compose UI Test framework available for UI tests
