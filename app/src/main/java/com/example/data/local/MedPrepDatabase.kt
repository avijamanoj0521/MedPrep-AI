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
    version = 3,
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
                            // Populate accredited curricula, question banks, and clinical reference cases
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).medPrepDao()
                                dao.insertSubjects(SeedData.initialSubjects())
                                dao.insertUnits(SeedData.initialUnits())
                                dao.insertChapters(SeedData.initialChapters())
                                dao.insertQuestions(SeedData.initialQuestions())
                                dao.insertClinicalCases(SeedData.initialClinicalCases())
                                for (card in SeedData.initialFlashcards()) {
                                    dao.insertFlashcard(card)
                                }
                                // No hardcoded student profile or active session is created by default.
                                // Every student starts fresh and signs into their own personal account.
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
