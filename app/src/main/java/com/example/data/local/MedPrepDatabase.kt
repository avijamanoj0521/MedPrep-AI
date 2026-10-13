package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        SubjectEntity::class,
        UnitEntity::class,
        ChapterEntity::class,
        StudySessionEntity::class,
        PersonalNoteEntity::class,
        QuizQuestionEntity::class,
        QuizAttemptEntity::class,
        ClinicalCaseEntity::class,
        NursingCarePlanEntity::class,
        FlashcardEntity::class,
        AiChatMessageEntity::class,
        UserAccountEntity::class,
        ClinicalWardLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MedPrepDatabase : RoomDatabase() {

    abstract fun medPrepDao(): MedPrepDao

    companion object {
        @Volatile
        private var INSTANCE: MedPrepDatabase? = null

        fun getDatabase(context: Context): MedPrepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedPrepDatabase::class.java,
                    "medprep_ai_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate seed data in background
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).medPrepDao()
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
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
