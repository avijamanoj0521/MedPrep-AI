package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiTutorEngine
import com.example.data.local.MedPrepDatabase
import com.example.data.local.SeedData
import com.example.data.model.*
import com.example.data.repository.MedPrepRepository
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

class MedPrepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedPrepRepository

    val userProfile: StateFlow<UserProfileEntity?>
    val activeUserAccount: StateFlow<UserAccountEntity?>
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

    init {
        val database = MedPrepDatabase.getDatabase(application)
        repository = MedPrepRepository(database.medPrepDao())

        userProfile = repository.userProfile.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        activeUserAccount = repository.activeUserAccount.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        wardLogs = repository.wardLogs.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        studySessions = repository.studySessions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        personalNotes = repository.allNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        quizAttempts = repository.quizAttempts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        clinicalCases = repository.clinicalCases.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        nursingCarePlans = repository.nursingCarePlans.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        flashcards = repository.flashcards.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        chatMessages = repository.chatMessages.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    // Dynamic subjects based on profile and selected semester
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

    val allCourseSubjects: StateFlow<List<SubjectEntity>> = userProfile.flatMapLatest { profile ->
        val courseId = profile?.courseId ?: "bsc_nursing"
        repository.getSubjectsForCourse(courseId)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val unitsForSelectedSubject: StateFlow<List<UnitEntity>> = _selectedSubject.flatMapLatest { subj ->
        if (subj != null) {
            repository.getUnits(subj.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
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
        curriculumVersion: String
    ) {
        viewModelScope.launch {
            val updated = UserProfileEntity(
                fullName = fullName,
                email = email,
                courseId = courseId,
                courseName = courseName,
                university = university,
                collegeName = college,
                admissionBatch = admissionBatch,
                academicYear = academicYear,
                semesterOrPhase = semesterOrPhase,
                curriculumVersion = curriculumVersion,
                isOnboarded = true,
                studyStreakDays = 5,
                totalStudyMinutes = 240
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
        viewModelScope.launch {
            repository.addStudySession(
                StudySessionEntity(
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
        viewModelScope.launch {
            val profile = userProfile.value
            repository.saveNote(
                PersonalNoteEntity(
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
        viewModelScope.launch {
            repository.recordQuizAttempt(
                QuizAttemptEntity(
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
        viewModelScope.launch {
            repository.addCarePlan(
                NursingCarePlanEntity(
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
        viewModelScope.launch {
            repository.addFlashcard(
                FlashcardEntity(
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

    // AI Tutor Chat
    fun sendAiPrompt(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val userMsg = AiChatMessageEntity(
                role = "user",
                content = trimmed,
                topicSubject = _selectedSubject.value?.name ?: "General Healthcare",
                timestampMillis = System.currentTimeMillis()
            )
            repository.saveChatMessage(userMsg)

            _isAiThinking.value = true

            // Generate verified response
            val responseText = AiTutorEngine.generateEducationalResponse(trimmed, userProfile.value)

            val aiMsg = AiChatMessageEntity(
                role = "assistant",
                content = responseText,
                topicSubject = _selectedSubject.value?.name ?: "General Healthcare",
                timestampMillis = System.currentTimeMillis()
            )
            repository.saveChatMessage(aiMsg)
            _isAiThinking.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
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

    // Student Authentication
    fun loginStudent(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { account ->
                // Update profile name and email
                val currProfile = userProfile.value
                if (currProfile != null) {
                    repository.saveProfile(currProfile.copy(fullName = account.fullName, email = account.email))
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
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(email, password, fullName, collegeName, courseId)
            result.onSuccess { account ->
                val courseProg = SeedData.supportedCourses.find { it.id == courseId } ?: SeedData.supportedCourses.first()
                val newProfile = UserProfileEntity(
                    fullName = fullName,
                    email = account.email,
                    courseId = courseId,
                    courseName = courseProg.name,
                    university = "State University of Health Sciences",
                    collegeName = collegeName,
                    admissionBatch = "2024-2028 Batch",
                    academicYear = "1st Year",
                    semesterOrPhase = courseProg.semestersOrPhases.firstOrNull() ?: "Semester 1",
                    curriculumVersion = "Official Curriculum Standard",
                    isOnboarded = true,
                    studyStreakDays = 1,
                    totalStudyMinutes = 60
                )
                repository.saveProfile(newProfile)
                _selectedSemester.value = newProfile.semesterOrPhase
                callback(true, null)
            }.onFailure { err ->
                callback(false, err.message)
            }
        }
    }

    fun logoutStudent() {
        viewModelScope.launch {
            repository.logout()
            _currentScreen.value = Screen.Auth
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
        viewModelScope.launch {
            repository.addWardLog(
                ClinicalWardLogEntity(
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

    // Note Updating
    fun updatePersonalNote(note: PersonalNoteEntity) {
        viewModelScope.launch {
            repository.saveNote(note)
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
