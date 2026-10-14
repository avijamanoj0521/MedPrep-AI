package com.example.data.repository

import com.example.data.local.MedPrepDao
import com.example.data.local.SeedData
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MedPrepRepository(private val dao: MedPrepDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfileFlow()

    fun getProfileForUser(email: String): Flow<UserProfileEntity?> = dao.getProfileByEmailFlow(email)

    suspend fun getProfileByEmail(email: String): UserProfileEntity? = withContext(Dispatchers.IO) {
        dao.getProfileByEmail(email)
    }

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        // Only seed academic curricula and question banks if empty
        val existingQuestions = dao.getRandomQuestions(1)
        if (existingQuestions.isEmpty()) {
            dao.insertSubjects(SeedData.initialSubjects())
            dao.insertUnits(SeedData.initialUnits())
            dao.insertChapters(SeedData.initialChapters())
            dao.insertQuestions(SeedData.initialQuestions())
            dao.insertClinicalCases(SeedData.initialClinicalCases())
            for (card in SeedData.initialFlashcards()) {
                dao.insertFlashcard(card)
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

    fun getStudySessionsForUser(email: String): Flow<List<StudySessionEntity>> =
        dao.getStudySessionsForUser(email)

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

    fun getNotesForUser(email: String): Flow<List<PersonalNoteEntity>> =
        dao.getNotesForUser(email)

    fun getNotesForCourse(courseId: String): Flow<List<PersonalNoteEntity>> =
        dao.getNotesForCourse(courseId)

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
    val allQuestions: Flow<List<QuizQuestionEntity>> = dao.getAllQuestions()

    fun getQuestionsForCourse(courseId: String): Flow<List<QuizQuestionEntity>> =
        dao.getQuestionsForCourse(courseId)

    fun getQuestionsForSubject(courseId: String, subjectName: String): Flow<List<QuizQuestionEntity>> =
        dao.getQuestionsForSubject(courseId, subjectName)

    suspend fun getRandomQuestions(count: Int): List<QuizQuestionEntity> = withContext(Dispatchers.IO) {
        dao.getRandomQuestions(count)
    }

    val quizAttempts: Flow<List<QuizAttemptEntity>> = dao.getAllQuizAttempts()

    fun getQuizAttemptsForUser(email: String): Flow<List<QuizAttemptEntity>> =
        dao.getQuizAttemptsForUser(email)

    suspend fun recordQuizAttempt(attempt: QuizAttemptEntity) = withContext(Dispatchers.IO) {
        dao.insertQuizAttempt(attempt)
    }

    // Clinical Learning
    val clinicalCases: Flow<List<ClinicalCaseEntity>> = dao.getAllClinicalCases()
    val nursingCarePlans: Flow<List<NursingCarePlanEntity>> = dao.getAllCarePlans()

    fun getCarePlansForUser(email: String): Flow<List<NursingCarePlanEntity>> =
        dao.getCarePlansForUser(email)

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

    fun getChatMessagesForUser(email: String): Flow<List<AiChatMessageEntity>> =
        dao.getChatMessagesForUser(email)

    suspend fun saveChatMessage(message: AiChatMessageEntity) = withContext(Dispatchers.IO) {
        dao.insertChatMessage(message)
    }

    suspend fun clearChatForUser(email: String) = withContext(Dispatchers.IO) {
        dao.clearChatForUser(email)
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        dao.clearChatHistory()
    }

    // Authentication & Sessions
    val activeUserAccount: Flow<UserAccountEntity?> = dao.getActiveUserAccountFlow()

    suspend fun getActiveUserAccount(): UserAccountEntity? = withContext(Dispatchers.IO) {
        dao.getActiveUserAccount()
    }

    suspend fun login(email: String, password: String): Result<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val account = dao.getUserAccount(cleanEmail)
        if (account == null) {
            Result.failure(Exception("No account registered for '$cleanEmail'. Please switch to Register."))
        } else if (account.passwordHash != password) {
            Result.failure(Exception("Incorrect password for this student account. Please check credentials or reset password."))
        } else {
            dao.logoutAllAccounts()
            dao.setAccountActive(cleanEmail)
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
            Result.failure(Exception("An account with this college email ($cleanEmail) already exists. Please login instead."))
        } else {
            val newAccount = UserAccountEntity(
                email = cleanEmail,
                passwordHash = password,
                fullName = fullName.trim(),
                collegeName = collegeName.trim(),
                courseId = courseId,
                isActiveSession = true
            )
            dao.logoutAllAccounts()
            dao.insertUserAccount(newAccount)
            Result.success(newAccount)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = dao.getUserAccount(cleanEmail)
        if (existing == null) {
            Result.failure(Exception("No registered account found with email '$cleanEmail'."))
        } else {
            dao.updatePassword(cleanEmail, newPassword)
            Result.success(Unit)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        dao.logoutAllAccounts()
    }

    // Ward Logs
    val wardLogs: Flow<List<ClinicalWardLogEntity>> = dao.getAllWardLogs()

    fun getWardLogsForUser(email: String): Flow<List<ClinicalWardLogEntity>> =
        dao.getWardLogsForUser(email)

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
