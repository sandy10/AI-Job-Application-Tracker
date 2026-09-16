# AI Job Application Tracker
> Your personalized AI job-search copilot that tracks every application, understands every opportunity, and tells you exactly what to do next.

AI Job Application Tracker is an **Android-first workspace** tailored for students, freshers, and experienced professionals to seamlessly manage their entire job-hunting lifecycle. Rather than just being a passive spreadsheet, it acts as an intelligent assistant that actively analyzes job descriptions, evaluates your resume, preps you for interviews, and generates follow-up emails using **Google Gemini AI**.

---

## 🚀 Key Features & App Flow

### 1. Authentication & Onboarding
* **Splash & Login**: Secure Firebase Authentication supporting seamless email/password login.
* **Career Setup**: Quickly set up your profile parameters (Experience Level, Current/Expected CTC, Skills, Primary Role) so the AI can give personalized recommendations.

### 2. Dashboard (Home)
* **Command Center**: View your job search at a glance—total applications, upcoming interviews, and offers.
* **Needs Attention**: AI-generated smart "Next Actions" that tell you exactly what you need to do today (e.g., "Follow up with ABC Corp", "Prepare for tomorrow's Google interview").

### 3. Application Pipeline
* **Track Everything**: Manage job entries through stages: *Saved → Applied → Recruiter Contact → Interview → Offer → Rejected*.
* **Smart Entry**: Add a job manually, or simply paste the Job Description text and let the **AI Job Analyzer** automatically extract the Company, Role, Location, Skills, and Salary!
* **Detailed Job Hub**: For every job, view timelines, attached resumes, scheduled interviews, and personal notes all on one screen.

### 4. AI-Powered Intelligence (Gemini)
* **AI Resume Match**: Compares your selected resume against a job description. The AI generates a compatibility score out of 100%, highlights matched skills, and exposes critical skill gaps.
* **AI Interview Prep**: Generates a tailored interview plan based on the job requirements and your experience level, suggesting focus areas and likely behavioral/technical questions.
* **Live Video AI Mock Interview**: Practice your pitch! Activate your camera and microphone to engage in a continuous, live simulated interview with the AI, which generates follow-up questions dynamically based on your spoken answers, and provides a final scored evaluation out of 10.
* **AI Follow-Up**: Automatically drafts context-aware emails to recruiters (e.g., post-interview thank you emails or status check-ins) that you can copy to your clipboard in one tap.

### 5. Profile, Analytics & Stability
* **My Resumes**: Upload and manage different versions of your resume (e.g., Android Lead vs. KMP Developer) stored securely in Firebase Cloud Storage.
* **Job Search Analytics**: Review your conversion rates (Application → Interview → Offer) to see where your funnel needs improvement.
* **App Stability & Tracking**: Integrated with **Google Analytics** for seamless user-journey tracking, and **Firebase Crashlytics** for real-time monitoring of AI timeouts, Firestore failures, and fatal crashes, with user-scoped crash reporting.
* **Settings & Logout**: Manage app notifications, update career profiles, or securely log out.

---

## 🛠️ Tech Stack & Architecture

This project strictly adheres to modern Android development standards and **Clean Architecture**:

* **UI**: Jetpack Compose & Material 3 (Custom Typography with Plus Jakarta Sans)
* **Text Formatting**: Native Markdown parsing for AI chat responses
* **State Management**: Lifecycle-aware state collection (collectAsStateWithLifecycle)
* **Language**: 100% Kotlin
* **Architecture**: Clean Architecture (Strict separation of Domain/Presentation Models) + MVVM
* **Asynchronous**: Kotlin Coroutines & StateFlow
* **Dependency Injection**: Hilt
* **Navigation**: Jetpack Navigation Compose
* **AI Engine**: Firebase Vertex AI (`gemini-2.5-flash`)
* **Backend / Database**: Firebase (Firestore, Authentication, Storage)
* **Analytics & Stability**: Google Analytics (Event tracking) & Firebase Crashlytics (Fatal and non-fatal AI error logging)
* **Audio/Speech**: Android native `SpeechRecognizer` and `TextToSpeech` API for the Video AI mock interviews.
* **Localization**: 100% UI strings extracted to `strings.xml` with centralized Constants.

---

## 📂 Project Structure

```
com.sandeep.aijobapplicationtracker/
├── data/
│   ├── repository/    → Implementation of data operations & AI calls
│   └── datastore/     → Local preferences (DataStore)
├── domain/
│   ├── model/         → Business logic objects
│   └── repository/    → Interfaces
├── presentation/
│   ├── screens/       → All UI Composables, organized by feature
│   ├── components/    → Reusable Compose UI elements (AppButton, AppTextField, etc.)
│   ├── navigation/    → AppNavGraph and Screen routes
│   └── theme/         → Material 3 Colors, Typography, Shapes
├── di/                → Hilt Modules (AppModule, FirebaseModule)
└── utils/             → Helper classes (VoiceHelper, Permissions, Constants)
```

---

## 💡 How to Run Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SandeepSudhakar/AI-Job-Application-Tracker.git
   ```
2. **Open in Android Studio**:
   Open the project using the latest stable release of Android Studio (Koala or newer).
3. **Connect Firebase**:
   * Add your `google-services.json` file to the `app/` directory.
   * Ensure Firestore and Firebase Authentication (Email/Password) are enabled in your Firebase Console.
4. **Build & Run**:
   Sync Gradle and run the app on an emulator or physical device running Android 8.0 (API 26) or higher.

> **Note on Permissions**: The Video AI Mock Interview feature requires explicit `CAMERA` and `RECORD_AUDIO` permissions to function. All speech processing is handled locally via the Android Speech API before being sent securely to Gemini for evaluation.

---

*Designed and engineered as a comprehensive smart-assistant for modern job seekers.*
