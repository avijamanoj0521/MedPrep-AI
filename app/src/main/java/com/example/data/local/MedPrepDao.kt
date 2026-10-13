package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MedPrepDao {

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 'default_student' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = 'default_student' LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Subjects
    @Query("SELECT * FROM subjects WHERE courseId = :courseId ORDER BY semesterOrPhase, name")
    fun getSubjectsByCourse(courseId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE courseId = :courseId AND semesterOrPhase = :semesterOrPhase ORDER BY name")
    fun getSubjectsByCourseAndSemester(courseId: String, semesterOrPhase: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    suspend fun getSubjectById(subjectId: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    // Units
    @Query("SELECT * FROM units WHERE subjectId = :subjectId ORDER BY unitNumber")
    fun getUnitsForSubject(subjectId: String): Flow<List<UnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Update
    suspend fun updateUnit(unit: UnitEntity)

    // Chapters
    @Query("SELECT * FROM chapters WHERE unitId = :unitId ORDER BY chapterNumber")
    fun getChaptersForUnit(unitId: String): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    // Timetable / Study Sessions
    @Query("SELECT * FROM study_sessions ORDER BY id DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity)

    @Update
    suspend fun updateStudySession(session: StudySessionEntity)

    @Delete
    suspend fun deleteStudySession(session: StudySessionEntity)

    // Notes
    @Query("SELECT * FROM personal_notes ORDER BY updatedAtMillis DESC")
    fun getAllNotes(): Flow<List<PersonalNoteEntity>>

    @Query("SELECT * FROM personal_notes WHERE courseId = :courseId ORDER BY updatedAtMillis DESC")
    fun getNotesForCourse(courseId: String): Flow<List<PersonalNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: PersonalNoteEntity)

    @Update
    suspend fun updateNote(note: PersonalNoteEntity)

    @Delete
    suspend fun deleteNote(note: PersonalNoteEntity)

    // Quizzes & Questions
    @Query("SELECT * FROM quiz_questions WHERE courseId = :courseId")
    fun getQuestionsForCourse(courseId: String): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE courseId = :courseId AND subjectName = :subjectName")
    fun getQuestionsForSubject(courseId: String, subjectName: String): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions LIMIT :count")
    suspend fun getRandomQuestions(count: Int): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    // Quiz Attempts
    @Query("SELECT * FROM quiz_attempts ORDER BY completedAtMillis DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)

    // Clinical Cases
    @Query("SELECT * FROM clinical_cases")
    fun getAllClinicalCases(): Flow<List<ClinicalCaseEntity>>

    @Query("SELECT * FROM clinical_cases WHERE targetCourse = :courseType")
    fun getClinicalCasesByTarget(courseType: String): Flow<List<ClinicalCaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicalCases(cases: List<ClinicalCaseEntity>)

    // Nursing Care Plans
    @Query("SELECT * FROM nursing_care_plans ORDER BY createdAtMillis DESC")
    fun getAllCarePlans(): Flow<List<NursingCarePlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarePlan(ncp: NursingCarePlanEntity)

    @Delete
    suspend fun deleteCarePlan(ncp: NursingCarePlanEntity)

    // Flashcards
    @Query("SELECT * FROM flashcards ORDER BY lastReviewedMillis ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE category = :category ORDER BY lastReviewedMillis ASC")
    fun getFlashcardsByCategory(category: String): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    // AI Chat History
    @Query("SELECT * FROM ai_chat_messages ORDER BY timestampMillis ASC")
    fun getAllAiChatMessages(): Flow<List<AiChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: AiChatMessageEntity)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearChatHistory()

    // User Accounts & Authentication
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserAccount(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE isActiveSession = 1 LIMIT 1")
    fun getActiveUserAccountFlow(): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET isActiveSession = 0")
    suspend fun logoutAllAccounts()

    @Query("UPDATE user_accounts SET isActiveSession = 1 WHERE email = :email")
    suspend fun setAccountActive(email: String)

    // Clinical Ward Logs
    @Query("SELECT * FROM clinical_ward_logs ORDER BY createdAtMillis DESC")
    fun getAllWardLogs(): Flow<List<ClinicalWardLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardLog(log: ClinicalWardLogEntity)

    @Delete
    suspend fun deleteWardLog(log: ClinicalWardLogEntity)
}
