# study-assistant-app

# Study Assistant Android App

A native Android application developed using Kotlin and Jetpack Compose to assist students with study workflow, task organization, and AI-powered study assistance.

---

## 🛠️ Features

- **Modern UI:** Built entirely with Jetpack Compose for reactive layout rendering.
- **Navigation Architecture:** Multi-screen navigation implementation for smooth user flow.
- **Task Management:** Core logic for tracking study routines, tasks, and schedules.
- **AI Integration:** Integrated with Google's Gemini API to provide smart study assistance.

---

## 🔑 Setup & API Configuration

This app uses the Gemini API for AI-assisted study features. For security reasons, sensitive keys are not included in this repository.

1. Obtain a Gemini API key from [Google AI Studio](https://aistudio.google.com/).
2. Clone this repository to your local machine.
3. Open the project in Android Studio.
4. Locate the API configuration file (e.g., `MainActivity.kt` or your client setup file) and replace `"YOUR_ACTUAL_API_KEY_HERE"` with your actual API key.
5. Build and run the project.

---

## 💻 Tech Stack & Tools

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **AI Backend:** Google Gemini API
- **IDE:** Android Studio
- **Build System:** Gradle (Kotlin DSL)

---

## 📁 Repository Structure

- `app/` - Application module containing UI components, navigation, and API logic
- `.gitignore` - Standard Android ignore rules filtering out local build outputs and cache
- `build.gradle.kts` - Project-level Gradle build configuration
- `settings.gradle.kts` - Plugin and repository management settings
