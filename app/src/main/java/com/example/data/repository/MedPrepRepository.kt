package com.example.data.repository

import com.example.data.local.MedPrepDao
import com.example.data.local.SeedData
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MedPrepRepository(private val dao: MedPrepDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfileFlow()

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfile()
        if (profile == null) {
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    fullName = "Priya Sharma",
                    email = "priya.nursing@medprep.edu",
                    courseId = "bsc_nursing",
                    courseName = "B.Sc Nursing (Bachelor of Science in Nursing)",
                    university = "Rajiv Gandhi University of Health Sciences",
                    collegeName = "Government College of Nursing, Bengaluru",
                    admissionBatch = "2023-2027 Batch",
                    academicYear = "1st Year",
                    semesterOrPhase = "Semester 1",
                    curriculumVersion = "INC Revised CBME Curriculum 2021",
                    isOnboarded = true,
                    studyStreakDays = 5,
                    totalStudyMinutes = 240
                )
            )
            dao.insertSubjects(SeedData.initialSubjects())
            dao.insertUnits(SeedData.initialUnits())
            dao.insertChapters(SeedData.initialChapters())
            for (session in SeedData.initialStudySessions()) {
                dao.insertStudySession(session)
            }
            for (note in SeedData.initialPersonalNotes()) {
                dao.insertNote(note)
            }
            dao.insertQuestions(SeedData.initialQuestions())
            dao.insertClinicalCases(SeedData.initialClinicalCases())
            for (plan in SeedData.initialNursingCarePlans()) {
                dao.insertCarePlan(plan)
            }
            for (card in SeedData.initialFlashcards()) {
                dao.insertFlashcard(card)
            }
            dao.insertUserAccount(SeedData.defaultUserAccount())
            for (ward in SeedData.initialWardLogs()) {
                dao.insertWardLog(ward)
            }
        }
    }

    suspend fun saveProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(profile)
    }

    fun getSubjectsForCourse(courseId: String): Flow<List<SubjectEntity>> =
        dao.getSubjectsByCourse(courseId)

    fun getSubjectsForCourseAndSemester(courseId: String, semester: String): Flow<List<SubjectEntity>> =
        dao.getSubjectsByCourseAndSemester(courseId, semester)

    suspend fun getSubjectById(subjectId: String): SubjectEntity? = withContext(Dispatchers.IO) {
        dao.getSubjectById(subjectId)
    }

    fun getUnits(subjectId: String): Flow<List<UnitEntity>> =
        dao.getUnitsForSubject(subjectId)

    fun getChapters(unitId: String): Flow<List<ChapterEntity>> =
        dao.getChaptersForUnit(unitId)

    suspend fun toggleChapterCompletion(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        val updated = chapter.copy(isCompleted = !chapter.isCompleted)
        dao.updateChapter(updated)
    }

    suspend fun insertCustomSubject(subject: SubjectEntity) = withContext(Dispatchers.IO) {
        dao.insertSubjects(listOf(subject))
    }

    suspend fun insertCustomUnit(unit: UnitEntity) = withContext(Dispatchers.IO) {
        dao.insertUnits(listOf(unit))
    }

    // Timetable
    val studySessions: Flow<List<StudySessionEntity>> = dao.getAllStudySessions()

    suspend fun addStudySession(session: StudySessionEntity) = withContext(Dispatchers.IO) {
        dao.insertStudySession(session)
    }

    suspend fun toggleSessionCompleted(session: StudySessionEntity) = withContext(Dispatchers.IO) {
        dao.updateStudySession(session.copy(isCompleted = !session.isCompleted))
    }

    suspend fun deleteStudySession(session: StudySessionEntity) = withContext(Dispatchers.IO) {
        dao.deleteStudySession(session)
    }

    // Notes
    val allNotes: Flow<List<PersonalNoteEntity>> = dao.getAllNotes()

    suspend fun saveNote(note: PersonalNoteEntity) = withContext(Dispatchers.IO) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(note: PersonalNoteEntity) = withContext(Dispatchers.IO) {
        dao.deleteNote(note)
    }

    suspend fun toggleNoteFavorite(note: PersonalNoteEntity) = withContext(Dispatchers.IO) {
        dao.updateNote(note.copy(isFavorite = !note.isFavorite))
    }

    // Quizzes
    fun getQuestionsForCourse(courseId: String): Flow<List<QuizQuestionEntity>> =
        dao.getQuestionsForCourse(courseId)

    suspend fun getRandomQuestions(count: Int): List<QuizQuestionEntity> = withContext(Dispatchers.IO) {
        dao.getRandomQuestions(count)
    }

    val quizAttempts: Flow<List<QuizAttemptEntity>> = dao.getAllQuizAttempts()

    suspend fun recordQuizAttempt(attempt: QuizAttemptEntity) = withContext(Dispatchers.IO) {
        dao.insertQuizAttempt(attempt)
    }

    // Clinical Learning
    val clinicalCases: Flow<List<ClinicalCaseEntity>> = dao.getAllClinicalCases()
    val nursingCarePlans: Flow<List<NursingCarePlanEntity>> = dao.getAllCarePlans()

    suspend fun addCarePlan(plan: NursingCarePlanEntity) = withContext(Dispatchers.IO) {
        dao.insertCarePlan(plan)
    }

    suspend fun deleteCarePlan(plan: NursingCarePlanEntity) = withContext(Dispatchers.IO) {
        dao.deleteCarePlan(plan)
    }

    // Flashcards
    val flashcards: Flow<List<FlashcardEntity>> = dao.getAllFlashcards()

    suspend fun addFlashcard(flashcard: FlashcardEntity) = withContext(Dispatchers.IO) {
        dao.insertFlashcard(flashcard)
    }

    suspend fun updateFlashcardMastery(flashcard: FlashcardEntity, level: Int) = withContext(Dispatchers.IO) {
        dao.updateFlashcard(flashcard.copy(masteryLevel = level, lastReviewedMillis = System.currentTimeMillis()))
    }

    // AI Chat
    val chatMessages: Flow<List<AiChatMessageEntity>> = dao.getAllAiChatMessages()

    suspend fun saveChatMessage(message: AiChatMessageEntity) = withContext(Dispatchers.IO) {
        dao.insertChatMessage(message)
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        dao.clearChatHistory()
    }

    // Authentication & Sessions
    val activeUserAccount: Flow<UserAccountEntity?> = dao.getActiveUserAccountFlow()

    suspend fun login(email: String, password: String):Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val account = dao.getUserAccount(email.trim().lowercase())
        if (account == null) {
            Result.failure(Exception("Account not found. Please register your student account."))
        } else if (account.passwordHash != password) {
            Result.failure(Exception("Incorrect password. Please verify credentials."))
        } else {
            dao.logoutAllAccounts()
            dao.setAccountActive(account.email)
            Result.success(account.copy(isActiveSession = true))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        collegeName: String,
        courseId: String
    ): Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = dao.getUserAccount(cleanEmail)
        if (existing != null) {
            Result.failure(Exception("An account with this college email already exists. Please login."))
        } else {
            val newAccount = UserAccountEntity(
                email = cleanEmail,
                passwordHash = password,
                fullName = fullName,
                collegeName = collegeName,
                courseId = courseId,
                isActiveSession = true
            )
            dao.logoutAllAccounts()
            dao.insertUserAccount(newAccount)
            Result.success(newAccount)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        dao.logoutAllAccounts()
    }

    // Ward Logs
    val wardLogs: Flow<List<ClinicalWardLogEntity>> = dao.getAllWardLogs()

    suspend fun addWardLog(log: ClinicalWardLogEntity) = withContext(Dispatchers.IO) {
        dao.insertWardLog(log)
    }

    suspend fun deleteWardLog(log: ClinicalWardLogEntity) = withContext(Dispatchers.IO) {
        dao.deleteWardLog(log)
    }

    // Batch Syllabus Import
    suspend fun importSyllabusBatch(subjects: List<SubjectEntity>, units: List<UnitEntity>) = withContext(Dispatchers.IO) {
        dao.insertSubjects(subjects)
        dao.insertUnits(units)
    }
}
