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

#### 3. Application Pipeline
* **Track Everything**: Manage job entries through stages: *Saved → Applied → Recruiter Contact → Interview → Offer → Rejected*.
* **Smart Entry**: Add a job manually, or simply paste the Job Description text and let the **AI Job Analyzer** automatically extract the Company, Role, Location, Skills, and Salary!
* **Bulk Ingestion via CSV**: Seamlessly ingest bulk data for Jobs and corresponding Drafts. The system accepts Jobs in the `<id>, <from>, <to>, <type>, <description>` schema and Drafts in the `<id>, <jobId>, <type>, <contents>, <status>` schema, persisting them securely using **Firestore atomic batches** (`WriteBatch`).
* **Detailed Job Hub**: For every job, view timelines, attached resumes, scheduled interviews, and personal notes all on one screen.

### 4. AI-Powered Intelligence (Gemini)
* **AI Resume Match**: Compares your selected resume against a job description. The AI generates a compatibility score out of 100%, highlights matched skills, and exposes critical skill gaps.
* **AI Interview Prep**: Generates a tailored interview plan based on the job requirements and your experience level, suggesting focus areas and likely behavioral/technical questions.
* **Live Video AI Mock Interview**: Practice your pitch! Activate your camera and microphone to engage in a continuous, live simulated interview with the AI, which generates follow-up questions dynamically based on your spoken answers, and provides a final scored evaluation out of 10.
* **Context-Aware AI Cover Letters & Follow-Ups**: The system dynamically generates highly tailored cover letters and follow-up emails. It intelligently reads your personal profile (skills, experience) alongside the history of all past drafts generated for that specific job, yielding a seamless, context-aware communication pipeline!

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
* **Backend / Database**: Firebase (Firestore, Authentication, Storage, with WriteBatch atomic support)
* **Analytics & Stability**: Google Analytics (Event tracking) & Firebase Crashlytics (Fatal and non-fatal AI error logging)
* **Audio/Speech**: Android native `SpeechRecognizer` and `TextToSpeech` API for the Video AI mock interviews.
* **Localization**: 100% UI strings extracted to `strings.xml` with centralized Constants.

---

## 📁 Project Structure

```
com.sandeep.aijobapplicationtracker/
├── data/
│   ├── repository/    → Implementation of data operations, atomic batches, & AI calls
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

## 🚀 How to Run Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SandeepSudhakar/AI-Job-Application-Tracker.git
   ```
2. **Open in Android Studio**:
   Open the project using the latest stable release of Android Studio (Koala or newer).
3. **Connect Firebase & App Check**:
   * Add your `google-services.json` file to the `app/` directory.
   * Ensure Firestore and Firebase Authentication (Email/Password) are enabled in your Firebase Console.
   * **Firebase App Check (Crucial for testing on multiple devices)**: 
     * **The Scenario**: If you want to install this app on a friend's phone or a secondary device, you might notice that Vertex AI or Firestore stops working. This is because of Firebase App Check.
     * **How to Fix It**: Go to your Firebase Console -> **App Check** -> **APIs** (or **Products**). Find **Vertex AI** and **Firestore** in the list.
     * **Unenforced Mode**: If you see an option to "Enforce", do **NOT** click it yet. Leaving it "Unenforced" (the default state) means App Check is just monitoring. In Unenforced mode, the app will successfully work on *any* device you install the APK on. 
     * **Enforced Mode**: If it says "Enforced", Firebase will reject requests from unrecognized devices. To test on a new device while Enforced, you must plug the device in, run the app, copy the debug token from Android Studio's Logcat (`DebugAppCheckProvider`), and register that token in the Firebase Console under App Check -> Apps -> Manage Debug Tokens.
4. **Build & Run**:
   Sync Gradle and run the app on an emulator or physical device running Android 8.0 (API 26) or higher.

> **Note on Permissions**: The Video AI Mock Interview feature requires explicit `CAMERA` and `RECORD_AUDIO` permissions to function. All speech processing is handled locally via the Android Speech API before being sent securely to Gemini for evaluation.

---

## 🚀 Recent Updates

* **Next Actions Intelligence Enhancement**: Refined the "Needs Attention" algorithm. Next Actions now seamlessly parse multiple date formats (`dd MMM yyyy`, `dd/MM/yyyy`, `yyyy-MM-dd`) from CSV imports or manual entry, instantly generating "Apply Now" or "Follow-up Recommended" prompts for real-time tracking.
* **Bulk Import UI Polish**: Overhauled the Bulk Import test feature UI. Multi-line CSV text fields are now framed within perfectly sized native Compose `Card` components, minimizing unnecessary whitespace while retaining a clean, robust aesthetic.

---

*Designed and engineered as a comprehensive smart-assistant for modern job seekers.*
