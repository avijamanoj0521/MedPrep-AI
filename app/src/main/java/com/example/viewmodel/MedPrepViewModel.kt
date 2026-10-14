package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiTutorEngine
import com.example.data.local.MedPrepDatabase
import com.example.data.local.SeedData
import com.example.data.model.*
import com.example.data.repository.MedPrepRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen(val title: String, val route: String) {
    object Auth : Screen("Student Authentication", "auth")
    object Onboarding : Screen("Student Onboarding", "onboarding")
    object Dashboard : Screen("Dashboard", "dashboard")
    object Syllabus : Screen("Curriculum & Syllabus", "syllabus")
    object SubjectDetail : Screen("Subject Curriculum", "subject_detail")
    object Timetable : Screen("Study Planner", "timetable")
    object Notes : Screen("Study Notes", "notes")
    object Quizzes : Screen("Question Bank & Quizzes", "quizzes")
    object MockExam : Screen("University Mock Exam", "mock_exam")
    object ClinicalLab : Screen("Nursing & Clinical Lab", "clinical_lab")
    object AiTutor : Screen("MedPrep AI Tutor", "ai_tutor")
    object Flashcards : Screen("High-Yield Flashcards", "flashcards")
    object Analytics : Screen("Academic Analytics", "analytics")
    object AdminSyllabus : Screen("Curriculum Administration", "admin_syllabus")
    object ProfileSettings : Screen("Student Profile", "profile_settings")
    object AudioTranscribe : Screen("Clinical Voice Transcribe", "audio_transcribe")
}

@OptIn(ExperimentalCoroutinesApi::class)
class MedPrepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedPrepRepository

    val activeUserAccount: StateFlow<UserAccountEntity?>

    val userProfile: StateFlow<UserProfileEntity?>
    val wardLogs: StateFlow<List<ClinicalWardLogEntity>>
    val studySessions: StateFlow<List<StudySessionEntity>>
    val personalNotes: StateFlow<List<PersonalNoteEntity>>
    val quizAttempts: StateFlow<List<QuizAttemptEntity>>
    val clinicalCases: StateFlow<List<ClinicalCaseEntity>>
    val nursingCarePlans: StateFlow<List<NursingCarePlanEntity>>
    val flashcards: StateFlow<List<FlashcardEntity>>
    val chatMessages: StateFlow<List<AiChatMessageEntity>>

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedSemester = MutableStateFlow<String>("Semester 1")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedSubject: StateFlow<SubjectEntity?> = _selectedSubject.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    init {
        val database = MedPrepDatabase.getDatabase(application)
        repository = MedPrepRepository(database.medPrepDao())

        activeUserAccount = repository.activeUserAccount.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        // User Profile scoped to the currently active account email
        userProfile = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getProfileForUser(account.email)
            } else {
                flowOf(null)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        // Notes isolated to active student
        personalNotes = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getNotesForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Study sessions isolated to active student
        studySessions = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getStudySessionsForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Quiz attempts isolated to active student
        quizAttempts = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getQuizAttemptsForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Clinical Ward Logs isolated to active student
        wardLogs = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getWardLogsForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Nursing Care Plans isolated to active student
        nursingCarePlans = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getCarePlansForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // AI Chat Messages isolated to active student
        chatMessages = activeUserAccount.flatMapLatest { account ->
            if (account != null) {
                repository.getChatMessagesForUser(account.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Curricular Reference Content (available to all registered students)
        clinicalCases = repository.clinicalCases.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        flashcards = repository.flashcards.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    // Dynamic subjects based on student profile and selected semester
    val enrolledSubjects: StateFlow<List<SubjectEntity>> = combine(
        userProfile,
        _selectedSemester
    ) { profile, semester ->
        val courseId = profile?.courseId ?: "bsc_nursing"
        courseId to semester
    }.flatMapLatest { (courseId, semester) ->
        repository.getSubjectsForCourseAndSemester(courseId, semester)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Complete course syllabus
    val allCourseSubjects: StateFlow<List<SubjectEntity>> = userProfile.flatMapLatest { profile ->
        val courseId = profile?.courseId ?: "bsc_nursing"
        repository.getSubjectsForCourse(courseId)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Prescribed units for the currently selected subject in SubjectDetailScreen
    val unitsForSelectedSubject: StateFlow<List<UnitEntity>> = _selectedSubject.flatMapLatest { subject ->
        if (subject != null) {
            repository.getUnits(subject.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Dynamic Calculated Metrics for Dashboard (No Fake Data!)
    val studyStreakDays: StateFlow<Int> = studySessions.map { sessions ->
        val completed = sessions.filter { it.isCompleted }
        if (completed.isEmpty()) 0 else completed.map { it.dateString }.distinct().size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStudyMinutes: StateFlow<Int> = studySessions.map { sessions ->
        sessions.filter { it.isCompleted }.sumOf { it.durationMinutes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val quizzesTakenCount: StateFlow<Int> = quizAttempts.map { attempts ->
        attempts.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All available quiz questions for the student's program
    val allQuizQuestions: StateFlow<List<QuizQuestionEntity>> = repository.allQuestions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val questionsForCourse: StateFlow<List<QuizQuestionEntity>> = userProfile.flatMapLatest { profile ->
        val courseId = profile?.courseId ?: "bsc_nursing"
        repository.getQuestionsForCourse(courseId)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectSemester(semester: String) {
        _selectedSemester.value = semester
    }

    fun selectSubject(subject: SubjectEntity) {
        _selectedSubject.value = subject
        _currentScreen.value = Screen.SubjectDetail
    }

    fun selectSubjectById(subjectId: String) {
        viewModelScope.launch {
            val subject = repository.getSubjectById(subjectId)
                ?: SeedData.initialSubjects().find { it.id == subjectId }
            if (subject != null) {
                _selectedSubject.value = subject
                _currentScreen.value = Screen.SubjectDetail
            }
        }
    }

    fun getChaptersForUnit(unitId: String): Flow<List<ChapterEntity>> {
        return repository.getChapters(unitId)
    }

    fun toggleChapterCompletion(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.toggleChapterCompletion(chapter)
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        courseId: String,
        courseName: String,
        university: String,
        college: String,
        admissionBatch: String,
        academicYear: String,
        semesterOrPhase: String,
        curriculumVersion: String,
        educationLevel: String = "Undergraduate Healthcare",
        preferredLanguage: String = "English"
    ) {
        viewModelScope.launch {
            val updated = UserProfileEntity(
                id = email.trim().lowercase(),
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                educationLevel = educationLevel,
                university = university,
                courseId = courseId,
                courseName = courseName,
                collegeName = college.trim(),
                admissionBatch = admissionBatch,
                academicYear = academicYear,
                semesterOrPhase = semesterOrPhase,
                curriculumVersion = curriculumVersion,
                preferredLanguage = preferredLanguage,
                isOnboarded = true,
                studyStreakDays = studyStreakDays.value,
                totalStudyMinutes = totalStudyMinutes.value
            )
            repository.saveProfile(updated)
            _selectedSemester.value = semesterOrPhase
            _currentScreen.value = Screen.Dashboard
        }
    }

    // Timetable Actions
    fun addStudySession(
        subjectName: String,
        topicName: String,
        dateString: String,
        startTime: String,
        durationMinutes: Int,
        sessionType: String
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.addStudySession(
                StudySessionEntity(
                    userEmail = studentEmail,
                    subjectName = subjectName,
                    topicName = topicName,
                    dateString = dateString,
                    startTime = startTime,
                    durationMinutes = durationMinutes,
                    sessionType = sessionType,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleSessionCompleted(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.toggleSessionCompleted(session)
        }
    }

    fun deleteStudySession(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.deleteStudySession(session)
        }
    }

    // Notes Actions
    fun savePersonalNote(
        subjectName: String,
        unitName: String,
        title: String,
        content: String,
        category: String
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        val profile = userProfile.value
        viewModelScope.launch {
            repository.saveNote(
                PersonalNoteEntity(
                    userEmail = studentEmail,
                    courseId = profile?.courseId ?: "bsc_nursing",
                    subjectName = subjectName,
                    unitName = unitName,
                    title = title,
                    content = content,
                    category = category,
                    isFavorite = false,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun updatePersonalNote(note: PersonalNoteEntity) {
        viewModelScope.launch {
            repository.saveNote(note)
        }
    }

    fun deleteNote(note: PersonalNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun toggleNoteFavorite(note: PersonalNoteEntity) {
        viewModelScope.launch {
            repository.toggleNoteFavorite(note)
        }
    }

    // Quiz Submission
    fun recordQuizAttempt(
        testTitle: String,
        subjectName: String,
        score: Int,
        totalQuestions: Int,
        timeTakenSeconds: Int
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.recordQuizAttempt(
                QuizAttemptEntity(
                    userEmail = studentEmail,
                    testTitle = testTitle,
                    subjectName = subjectName,
                    score = score,
                    totalQuestions = totalQuestions,
                    timeTakenSeconds = timeTakenSeconds,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Builds a quiz of requested count (10 to 15 questions) with zero duplicates.
     */
    fun generateQuizQuestions(targetCount: Int, subjectFilter: String? = null): List<QuizQuestionEntity> {
        val count = targetCount.coerceIn(10, 15)
        val allAvailable = allQuizQuestions.value.ifEmpty { SeedData.initialQuestions() }

        val filtered = if (!subjectFilter.isNullOrBlank() && subjectFilter != "All Subjects") {
            allAvailable.filter { it.subjectName.equals(subjectFilter, ignoreCase = true) }
        } else {
            allAvailable
        }

        // If filtered has enough questions, take count
        if (filtered.size >= count) {
            return filtered.shuffled().distinctBy { it.id }.take(count)
        }

        // Otherwise supplement with questions from general bank so the quiz always has the full count
        val result = filtered.toMutableList()
        val remaining = allAvailable.filterNot { item -> result.any { it.id == item.id } }.shuffled()
        for (item in remaining) {
            if (result.size >= count) break
            result.add(item)
        }
        return result.take(count)
    }

    // Nursing Care Plan
    fun saveNursingCarePlan(
        patientInitials: String,
        medicalDiagnosis: String,
        subjectiveData: String,
        objectiveData: String,
        nandaDiagnosis: String,
        smartGoal: String,
        nursingInterventions: String,
        rationales: String,
        evaluation: String
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.addCarePlan(
                NursingCarePlanEntity(
                    userEmail = studentEmail,
                    patientInitials = patientInitials,
                    medicalDiagnosis = medicalDiagnosis,
                    subjectiveData = subjectiveData,
                    objectiveData = objectiveData,
                    nandaDiagnosis = nandaDiagnosis,
                    smartGoal = smartGoal,
                    nursingInterventions = nursingInterventions,
                    rationales = rationales,
                    evaluation = evaluation,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteCarePlan(plan: NursingCarePlanEntity) {
        viewModelScope.launch {
            repository.deleteCarePlan(plan)
        }
    }

    // Flashcard Actions
    fun saveFlashcard(
        subjectName: String,
        category: String,
        front: String,
        back: String
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.addFlashcard(
                FlashcardEntity(
                    userEmail = studentEmail,
                    subjectName = subjectName,
                    category = category,
                    front = front,
                    back = back,
                    masteryLevel = 1,
                    lastReviewedMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateFlashcardMastery(flashcard: FlashcardEntity, level: Int) {
        viewModelScope.launch {
            repository.updateFlashcardMastery(flashcard, level)
        }
    }

    // Real AI Tutor using Gemini 3.1 Flash-Lite
    fun sendAiPrompt(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isEmpty()) return
        val studentEmail = activeUserAccount.value?.email ?: ""

        viewModelScope.launch {
            val userMsg = AiChatMessageEntity(
                userEmail = studentEmail,
                role = "user",
                content = trimmed,
                topicSubject = _selectedSubject.value?.name ?: "General Healthcare",
                timestampMillis = System.currentTimeMillis()
            )
            repository.saveChatMessage(userMsg)

            _isAiThinking.value = true
            _aiError.value = null

            val history = chatMessages.value.takeLast(6).map { it.role to it.content }

            val result = AiTutorEngine.getTutorResponse(
                prompt = trimmed,
                profile = userProfile.value,
                selectedSubject = _selectedSubject.value?.name,
                conversationHistory = history
            )

            result.onSuccess { responseText ->
                val aiMsg = AiChatMessageEntity(
                    userEmail = studentEmail,
                    role = "assistant",
                    content = responseText,
                    topicSubject = _selectedSubject.value?.name ?: "General Healthcare",
                    timestampMillis = System.currentTimeMillis()
                )
                repository.saveChatMessage(aiMsg)
            }.onFailure { err ->
                val errorMsg = err.localizedMessage ?: "Unknown AI error occurred."
                _aiError.value = errorMsg
                val errorBubble = AiChatMessageEntity(
                    userEmail = studentEmail,
                    role = "assistant",
                    content = "⚠️ **AI Tutor Request Failed**\n$errorMsg\n\n*Check your network connection and verify your Gemini API key in the Secrets panel.*",
                    topicSubject = _selectedSubject.value?.name ?: "General Healthcare",
                    timestampMillis = System.currentTimeMillis()
                )
                repository.saveChatMessage(errorBubble)
            }

            _isAiThinking.value = false
        }
    }

    fun clearChat() {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.clearChatForUser(studentEmail)
        }
    }

    // Admin Syllabus
    fun addCustomSubject(
        name: String,
        code: String,
        credits: Int,
        theoryHours: Int,
        practicalHours: Int,
        clinicalHours: Int,
        semester: String,
        objectives: String,
        assessment: String,
        isVerified: Boolean
    ) {
        viewModelScope.launch {
            val profile = userProfile.value
            val courseId = profile?.courseId ?: "bsc_nursing"
            val id = "subj_${System.currentTimeMillis()}"
            val newSubject = SubjectEntity(
                id = id,
                courseId = courseId,
                academicYear = profile?.academicYear ?: "1st Year",
                semesterOrPhase = semester,
                name = name,
                code = if (code.isBlank()) "UNVERIFIED" else code,
                credits = credits,
                theoryHours = theoryHours,
                practicalHours = practicalHours,
                clinicalHours = clinicalHours,
                learningObjectives = objectives,
                assessmentPattern = assessment,
                isVerified = isVerified,
                isCompleted = false,
                progressPercentage = 0
            )
            repository.insertCustomSubject(newSubject)
        }
    }

    // Student Authentication & Session Management
    fun loginStudent(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { account ->
                // Ensure student profile exists
                val existingProfile = repository.getProfileByEmail(account.email)
                if (existingProfile == null) {
                    val courseProg = SeedData.supportedCourses.find { it.id == account.courseId }
                        ?: SeedData.supportedCourses.first()
                    val newProfile = UserProfileEntity(
                        id = account.email,
                        fullName = account.fullName,
                        email = account.email,
                        educationLevel = "Undergraduate Healthcare",
                        university = "State University of Health Sciences",
                        courseId = account.courseId,
                        courseName = courseProg.name,
                        collegeName = account.collegeName,
                        admissionBatch = "2024-2028 Batch",
                        academicYear = "1st Year",
                        semesterOrPhase = courseProg.semestersOrPhases.firstOrNull() ?: "Semester 1",
                        curriculumVersion = "Accredited Standard Curriculum",
                        preferredLanguage = "English",
                        isOnboarded = false,
                        studyStreakDays = 0,
                        totalStudyMinutes = 0
                    )
                    repository.saveProfile(newProfile)
                }
                callback(true, null)
            }.onFailure { err ->
                callback(false, err.message)
            }
        }
    }

    fun registerStudent(
        email: String,
        password: String,
        fullName: String,
        collegeName: String,
        courseId: String,
        educationLevel: String = "Undergraduate Healthcare",
        university: String = "State University of Health Sciences",
        academicYear: String = "1st Year",
        semesterOrPhase: String = "Semester 1",
        preferredLanguage: String = "English",
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(email, password, fullName, collegeName, courseId)
            result.onSuccess { account ->
                val courseProg = SeedData.supportedCourses.find { it.id == courseId } ?: SeedData.supportedCourses.first()
                val newProfile = UserProfileEntity(
                    id = account.email,
                    fullName = fullName.trim(),
                    email = account.email,
                    educationLevel = educationLevel,
                    university = university,
                    courseId = courseId,
                    courseName = courseProg.name,
                    collegeName = collegeName.trim(),
                    admissionBatch = "2024-2028 Batch",
                    academicYear = academicYear,
                    semesterOrPhase = semesterOrPhase,
                    curriculumVersion = "Accredited Standard Curriculum",
                    preferredLanguage = preferredLanguage,
                    isOnboarded = true,
                    studyStreakDays = 0,
                    totalStudyMinutes = 0
                )
                repository.saveProfile(newProfile)
                _selectedSemester.value = newProfile.semesterOrPhase
                callback(true, null)
            }.onFailure { err ->
                callback(false, err.message)
            }
        }
    }

    fun resetPassword(email: String, newPass: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPassword(email, newPass)
            result.onSuccess {
                callback(true, null)
            }.onFailure { err ->
                callback(false, err.message)
            }
        }
    }

    fun logoutStudent(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logout()
            _selectedSubject.value = null
            _currentScreen.value = Screen.Auth
            onLoggedOut()
        }
    }

    // Ward Log Actions
    fun addWardLog(
        wardName: String,
        date: String,
        caseType: String,
        procedures: String,
        supervisor: String,
        reflection: String,
        hours: Int
    ) {
        val studentEmail = activeUserAccount.value?.email ?: ""
        viewModelScope.launch {
            repository.addWardLog(
                ClinicalWardLogEntity(
                    userEmail = studentEmail,
                    wardName = wardName,
                    postingDate = date,
                    patientCaseType = caseType,
                    proceduresAssisted = procedures,
                    supervisorName = supervisor,
                    clinicalReflection = reflection,
                    hoursLogged = hours,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteWardLog(log: ClinicalWardLogEntity) {
        viewModelScope.launch {
            repository.deleteWardLog(log)
        }
    }

    // Batch Syllabus Import
    fun importSyllabusBatch(subjects: List<SubjectEntity>, units: List<UnitEntity>, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.importSyllabusBatch(subjects, units)
                onDone(true, "Successfully imported ${subjects.size} subjects and ${units.size} curriculum units into syllabus database.")
            } catch (e: Exception) {
                onDone(false, "Import failed: ${e.localizedMessage}")
            }
        }
    }
}
