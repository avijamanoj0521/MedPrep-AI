# MEDPREP AI — Comprehensive College Healthcare Education Platform

MedPrep AI is a dedicated academic and clinical learning platform engineered for undergraduate and postgraduate students in **Medical (MBBS, BDS), Nursing (B.Sc Nursing, GNM, Post Basic, M.Sc), Pharmacy (B.Pharm, Pharm.D), Physiotherapy (BPT), and Allied Health Sciences** in colleges and universities.

Unlike entrance-exam coaching tools, MedPrep AI is built around the **actual semester-wise and professional-phase curricula** prescribed by regulatory councils (INC, NMC CBME, PCI, DCI).

---

## 🚀 Key Features

1. **Semester-Wise & Professional Phase Syllabus**:
   - Course-specific academic structure: Course → Academic Year / Phase → Semester → Subject → Unit → Chapter → High-Yield points.
   - Prescribed lecture hours, practical credits, clinical postings, and university assessment schemes.
   - Real distinction between verified official syllabi and unverified/sample programs.

2. **Student Academic Onboarding & Profile**:
   - Multi-step wizard capturing degree program, university, affiliated college, admission batch, and curriculum version.
   - Easy switching between courses and semesters without losing recorded progress.

3. **Clinical Learning & Nursing Care Lab**:
   - **NANDA-I Nursing Care Plan (NCP) Builder**: Create, document, and review care plans in standard **ADPIE** format (Assessment, Diagnosis, Planning/SMART goals, Implementation with rationales, Evaluation).
   - **Interactive Clinical Case Simulations**: Fictional patient scenarios (e.g., Acute Inferior STEMI, Post-op Wound Dehiscence, Pediatric Bronchiolitis) with vital signs and prioritized nursing interventions.
   - **High-Yield Pharmacology & Drug Classifications**: Mechanisms of action, contraindications, and nurse-monitoring precautions.
   - **Clinical Procedure Checklists (OSPE)**: IV Cannulation, Basic Life Support (BLS), Infection Control, and Catheterization protocols.

4. **Dedicated Healthcare AI Tutor**:
   - Conversational medical tutor adapting explanations to the student's enrolled program and semester.
   - Step-by-step physiological mechanisms (e.g., RAAS, Cardiac Cycle), viva voce practice questions, mnemonics, and pharmacology calculations.
   - Transparent clinical safety disclaimers for educational use.

5. **Study Planner & Timetable**:
   - Daily and weekly scheduler for theory lectures, ward postings, laboratory sessions, and revision tasks.
   - Interactive checklist with completion state saved persistently.

6. **Personal Clinical Notes**:
   - Searchable personal notes categorized by subject and unit.
   - Star favorite high-yield summaries and examination pearls.

7. **Question Bank & University Mock Exams**:
   - Subject-wise MCQ questions with immediate rationales and explanations.
   - Timed University Mock Exam simulator with 30-minute countdown, question palette (Answered, Marked for Review, Unattempted), and terminal assessment diagnostics.

8. **Spaced Repetition Flashcards**:
   - Animated flip cards for Anatomy, Pharmacology, and Normal Lab Values with recall ease ratings (Again, Hard, Good, Easy).

9. **Curriculum Administration & Import**:
   - Admin tool to publish new subjects, units, hours distribution, and verify curriculum standards.

10. **Microphone Audio Transcription (`gemini-3.5-transcribe`)**:
    - Speech-to-text voice dictation specifically optimized for medical and pharmacological terminology.
    - Dictate viva voce answers, ward posting patient observations, and study notes with immediate saving to Room SQLite database.
    - Model badge indicator and seamless on-device fallback.

---

## 🛠️ Technology Stack & Architecture

- **Operating System / Target**: Android 7.0+ (API 24 to 36)
- **UI Framework**: Modern Jetpack Compose with Material Design 3 (M3)
- **Programming Language**: Kotlin (Coroutines, Flow, StateFlow)
- **Architecture**: Clean MVVM (Model-View-ViewModel + Repository Pattern)
- **Database Persistence**: Android Jetpack Room (SQLite with reactive `Flow` updates and background migrations)
- **Theme & Design**: Medical Navy Deep (`#0F2C59`), Clinical Teal (`#00897B`), Medical Cyan Container (`#E0F2F1`), Soft Slate surfaces, accessible touch targets (48dp+), edge-to-edge support.

---

## 📁 Project Structure

```
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt               # Navigation host & edge-to-edge scaffold
│   │   │   ├── data/
│   │   │   │   ├── model/Entities.kt         # Room data entities (Profile, Subject, Unit, NCP, etc.)
│   │   │   │   ├── local/MedPrepDao.kt       # Reactive Room DAOs
│   │   │   │   ├── local/MedPrepDatabase.kt  # Pre-populated Room database singleton
│   │   │   │   ├── local/SeedData.kt         # INC/NMC regulatory curriculum seeds
│   │   │   │   ├── repository/MedPrepRepository.kt # Centralized abstracted data repository
│   │   │   │   └── ai/AiTutorEngine.kt       # Medical AI tutoring intelligence engine
│   │   │   ├── viewmodel/MedPrepViewModel.kt # State management & UI domain flows
│   │   │   ├── ui/
│   │   │   │   ├── theme/Color.kt, Theme.kt, Type.kt # MedPrep branded design tokens
│   │   │   │   ├── components/AppBars.kt     # TopAppBar, BottomNav, Badges
│   │   │   │   └── screens/
│   │   │   │       ├── DashboardScreen.kt
│   │   │   │       ├── OnboardingScreen.kt
│   │   │   │       ├── SyllabusScreen.kt
│   │   │   │       ├── SubjectDetailScreen.kt
│   │   │   │       ├── TimetableScreen.kt
│   │   │   │       ├── NotesScreen.kt
│   │   │   │       ├── QuizScreen.kt
│   │   │   │       ├── MockExamScreen.kt
│   │   │   │       ├── ClinicalLearningScreen.kt
│   │   │   │       ├── AiTutorScreen.kt
│   │   │   │       ├── FlashcardsScreen.kt
│   │   │   │       ├── AnalyticsScreen.kt
│   │   │   │       ├── AdminSyllabusScreen.kt
│   │   │   │       └── ProfileSettingsScreen.kt
│   │   └── res/                              # Adaptive icons, hero banners, layouts
│   └── build.gradle.kts                      # Dependencies and compiler plugins
├── gradle/
│   └── libs.versions.toml                    # Version catalog
└── metadata.json                             # AI Studio application metadata
```

---

## 🏃 Running & Testing the Application

### 1. Verification Build
Run the standard build verification via Gradle:
```bash
gradle assembleDebug
```

### 2. Unit & Robolectric Tests
Run JVM unit tests:
```bash
gradle :app:testDebugUnitTest
```

---

## 🔒 Privacy & Clinical Disclaimer
MedPrep AI is designed exclusively for educational simulations and academic university preparation. It must not be used as clinical diagnostic decision software or as authorization to administer unauthorized medications or procedures to actual patients without qualified institutional clinical supervision.
