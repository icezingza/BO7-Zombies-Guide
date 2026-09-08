# BO7 Zombies Guide — Rex Infernus Tactical Co-Pilot

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![AI](https://img.shields.io/badge/AI-Gemini%20Vision%20%2B%20CameraX-orange.svg)](https://ai.google.dev)

Tactical companion, real-time live video co-pilot, and complete easter egg walkthrough for the **Call of Duty Zombies** map **Rex Infernus**.

---

## 🌟 Key Features

1. **Live CameraX & Gemini AI Vision Co-Pilot**
   - Live stream analyzer from your console/PC screen (PS5/Xbox/PC).
   - Frame difference motion filtering to reduce bandwidth & redundant requests.
   - Hands-free Voice Commands ("สแกนทันที", "ขอสูตรบอส", "ขั้นตอนถัดไป").
   - Android Text-To-Speech (TTS) emergency audio callouts.
   - **Draggable PIP Mini-Overlay HUD Widget** for low-profile tactical gameplay.

2. **Main Quest & Survival Walkthrough**
   - 14 interactive steps with completion checkmarks, emergency swarm checklists, and step media.
   - Searchable and categorized by step difficulty and phase.

3. **Puzzle & Easter Egg Solvers**
   - **Veytharion 3D/2D Cube Solver** with cycle steps.
   - **Exfil House Symbols Matrix** (4-pillar slot tracker).
   - **4 Temples Purification Tracker**.
   - **Squad Share & Offline 2D Barcode QR Code Generator** for sharing codes with teammates in real-time.

4. **Boss Tactics & Event Timers**
   - Weakpoint analysis for the Warden boss.
   - Loadout readiness matrix.
   - Real-time countdowns: Warden Shield Phase (45s), Void Burst (20s), Hellhound Round (60s), Pack-a-Punch relocation (90s).

5. **Raid History Dashboard**
   - Offline-first persistence via **Room Database v3**.
   - Track high rounds, Exfil success counts, survival averages, and match notes.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 100%
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** Clean Architecture + MVVM + Repository Pattern
- **Persistence:** Android Jetpack Room Database (SQLite)
- **Camera:** Android Jetpack CameraX (ImageAnalysis & Preview)
- **Speech:** Android SpeechRecognizer + TextToSpeech (TTS)
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalog (`gradle/libs.versions.toml`)

---

## 🚀 How to Build APK

### Option A: Local Build with Android Studio
1. Clone this repository:
   ```bash
   git clone https://github.com/icezingza/BO7-Zombies-Guide.git
   ```
2. Open the project in **Android Studio Ladybug (or newer)**.
3. Allow Gradle to sync dependencies.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)** or run:
   ```bash
   ./gradlew assembleDebug
   ```
5. The generated APK will be available at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Option B: Automated Build via GitHub Actions
A GitHub Actions workflow is provided in `.github/workflows/build-apk.yml`. Every time code is pushed or a workflow is triggered manually, GitHub will build the APK and make it available under the **Actions > Artifacts** tab or create an automated Release.

---

## 🔐 Configuration & API Keys

To enable Gemini AI Vision features:
1. Provide your Gemini API key in your environment or Secrets:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
2. When building with Android Studio, the secret is automatically injected into `BuildConfig.GEMINI_API_KEY`.
