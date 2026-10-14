package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = "default_student",
    val fullName: String,
    val email: String,
    val educationLevel: String = "Undergraduate Healthcare",
    val university: String, // Board, University, or Licensing Examination
    val courseId: String,
    val courseName: String,
    val collegeName: String,
    val admissionBatch: String,
    val academicYear: String,
    val semesterOrPhase: String,
    val curriculumVersion: String,
    val preferredLanguage: String = "English",
    val isOnboarded: Boolean = true,
    val studyStreakDays: Int = 0,
    val totalStudyMinutes: Int = 0
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val academicYear: String,
    val semesterOrPhase: String,
    val name: String,
    val code: String,
    val credits: Int,
    val theoryHours: Int,
    val practicalHours: Int,
    val clinicalHours: Int,
    val learningObjectives: String,
    val assessmentPattern: String,
    val isVerified: Boolean = true, // false = SAMPLE/UNVERIFIED
    val isCompleted: Boolean = false,
    val progressPercentage: Int = 0
)

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val unitNumber: Int,
    val title: String,
    val description: String,
    val totalChapters: Int = 4,
    val completedChapters: Int = 0
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val unitId: String,
    val chapterNumber: Int,
    val title: String,
    val highYieldSummary: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val subjectName: String,
    val topicName: String,
    val dateString: String, // e.g. "Today", "Tomorrow", "Wednesday"
    val startTime: String, // e.g. "09:00 AM"
    val durationMinutes: Int,
    val sessionType: String, // "Theory", "Practical", "Clinical Posting", "Revision", "Assignment"
    val isCompleted: Boolean = false
)

@Entity(tableName = "personal_notes")
data class PersonalNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: String,
    val subjectName: String,
    val unitName: String,
    val title: String,
    val content: String,
    val category: String = "Lecture Note",
    val isFavorite: Boolean = false,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val subjectName: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0 to 3
    val explanation: String,
    val difficulty: String = "Medium", // Easy, Medium, High-Yield
    val isVerifiedExamQuestion: Boolean = true
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val testTitle: String,
    val subjectName: String,
    val score: Int,
    val totalQuestions: Int,
    val timeTakenSeconds: Int,
    val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "clinical_cases")
data class ClinicalCaseEntity(
    @PrimaryKey val id: String,
    val targetCourse: String, // e.g., "Nursing", "MBBS", "Allied Health"
    val title: String,
    val patientAgeGender: String,
    val chiefComplaint: String,
    val vitalSigns: String,
    val clinicalBackground: String,
    val diagnosticFindings: String,
    val priorityNursingInterventions: String,
    val safetyAndInfectionControl: String
)

@Entity(tableName = "nursing_care_plans")
data class NursingCarePlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val patientInitials: String,
    val medicalDiagnosis: String,
    val subjectiveData: String,
    val objectiveData: String,
    val nandaDiagnosis: String,
    val smartGoal: String,
    val nursingInterventions: String,
    val rationales: String,
    val evaluation: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val subjectName: String,
    val category: String, // Anatomy, Pharmacology, Lab Values, Nursing
    val front: String,
    val back: String,
    val masteryLevel: Int = 1, // 1 to 4
    val lastReviewedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_chat_messages")
data class AiChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val role: String, // "user", "assistant"
    val content: String,
    val topicSubject: String = "General Healthcare",
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val fullName: String,
    val collegeName: String,
    val courseId: String,
    val isActiveSession: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "clinical_ward_logs")
data class ClinicalWardLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val wardName: String,
    val postingDate: String,
    val patientCaseType: String,
    val proceduresAssisted: String,
    val supervisorName: String,
    val clinicalReflection: String,
    val hoursLogged: Int = 4,
    val createdAtMillis: Long = System.currentTimeMillis()
)
