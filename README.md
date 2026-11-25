# 🎸 StringTune – Chromatic Guitar Tuner

StringTune is an Android-based chromatic guitar tuner built using Kotlin. It uses the device's microphone to detect guitar string frequencies in real time and helps musicians tune their instruments accurately.

The project is being developed as a hands-on Android development project, with a focus on audio processing, pitch detection and custom UI development.

## ✨ Features

- **Real-time Pitch Detection** – Detects incoming audio frequencies through the device microphone.
- **Chromatic Note Recognition** – Identifies musical notes across all 12 chromatic pitches.
- **Live Frequency Display** – Displays the detected frequency in Hz.
- **Visual Tuning Gauge** – Custom semicircular gauge indicating whether a note is Flat, In Tune or Sharp.
- **Smooth Needle Animation** – Provides fluid visual feedback while tuning.
- **Live Cents Measurement** – Shows how far the detected pitch is from the nearest musical note.
- **Automatic Note Recognition** – No manual string selection required.
- **Microphone Lifecycle Management** – Starts and stops audio recording according to the app's lifecycle.

## 🛠️ Technologies Used

- Kotlin
- Android SDK
- XML Layouts
- Android AudioRecord API
- Custom Android Views
- ValueAnimator
- YIN-based Pitch Detection
- Material Design Components

## ⚙️ How It Works

StringTune captures audio through the device microphone using Android's `AudioRecord` API.

The captured audio samples are processed by a YIN-based pitch detection algorithm to estimate the fundamental frequency.

The detected frequency is then converted into its nearest musical note using the equal-tempered tuning system, with A4 set to 440 Hz.

The app calculates the pitch deviation in cents and updates the tuning gauge in real time.

## 🎵 Standard Guitar Tuning Reference

| String | Note | Frequency |
|---|---|---:|
| 6th | E2 | 82.41 Hz |
| 5th | A2 | 110.00 Hz |
| 4th | D3 | 146.83 Hz |
| 3rd | G3 | 196.00 Hz |
| 2nd | B3 | 246.94 Hz |
| 1st | E4 | 329.63 Hz |

## 📱 Requirements

- Android Studio
- Android SDK
- Minimum Android version: Android 6.0 (API 23)
- A device with a working microphone

Microphone permission is required for pitch detection.

## 🚀 Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/arkaPrabhaSarkar48/StringTune.git
   ```

2. Open the project in Android Studio.

3. Allow Gradle to sync.

4. Connect an Android device or start an emulator.

5. Build and run the application.

6. Grant microphone permission when prompted.

## 📂 Project Structure

```text
StringTune/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/app/stringtune/
│           │   ├── MainActivity.kt
│           │   ├── AudioRecorder.kt
│           │   ├── PitchDetector.kt
│           │   ├── NoteIdentifier.kt
│           │   └── TuningGaugeView.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── build.gradle.kts
└── settings.gradle.kts
```

## 🔧 Current Development Status

StringTune currently supports real-time chromatic pitch detection, note identification, frequency display and visual tuning feedback.

Further improvements in pitch stability, accuracy, UI refinement and testing are planned.

## 👨‍💻 Developer

**Arkaprabha Sarkar**

GitHub: https://github.com/arkaPrabhaSarkar48

---

*StringTune – Tune by ear, guided by precision.*