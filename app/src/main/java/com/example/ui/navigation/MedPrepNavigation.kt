package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

/**
 * Centralized Route definitions for the MedPrep AI Compose Navigation Graph.
 */
object MedPrepRoutes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SUBJECT_DETAIL = "subject_detail/{subjectId}"
    const val SUBJECT_DETAIL_BASE = "subject_detail"

    const val AUTH = "auth"
    const val SYLLABUS = "syllabus"
    const val TIMETABLE = "timetable"
    const val NOTES = "notes"
    const val QUIZZES = "quizzes"
    const val MOCK_EXAM = "mock_exam"
    const val CLINICAL_LAB = "clinical_lab"
    const val AI_TUTOR = "ai_tutor"
    const val FLASHCARDS = "flashcards"
    const val ANALYTICS = "analytics"
    const val ADMIN_SYLLABUS = "admin_syllabus"
    const val PROFILE_SETTINGS = "profile_settings"
    const val AUDIO_TRANSCRIBE = "audio_transcribe"

    fun subjectDetail(subjectId: String): String = "subject_detail/$subjectId"
}

/**
 * Maps Screen sealed class instance to Compose Navigation Route string.
 */
fun Screen.toRoute(subjectId: String? = null): String {
    return when (this) {
        Screen.Onboarding -> MedPrepRoutes.ONBOARDING
        Screen.Dashboard -> MedPrepRoutes.DASHBOARD
        Screen.SubjectDetail -> if (subjectId != null) MedPrepRoutes.subjectDetail(subjectId) else MedPrepRoutes.SUBJECT_DETAIL_BASE
        Screen.Auth -> MedPrepRoutes.AUTH
        Screen.Syllabus -> MedPrepRoutes.SYLLABUS
        Screen.Timetable -> MedPrepRoutes.TIMETABLE
        Screen.Notes -> MedPrepRoutes.NOTES
        Screen.Quizzes -> MedPrepRoutes.QUIZZES
        Screen.MockExam -> MedPrepRoutes.MOCK_EXAM
        Screen.ClinicalLab -> MedPrepRoutes.CLINICAL_LAB
        Screen.AiTutor -> MedPrepRoutes.AI_TUTOR
        Screen.Flashcards -> MedPrepRoutes.FLASHCARDS
        Screen.Analytics -> MedPrepRoutes.ANALYTICS
        Screen.AdminSyllabus -> MedPrepRoutes.ADMIN_SYLLABUS
        Screen.ProfileSettings -> MedPrepRoutes.PROFILE_SETTINGS
        Screen.AudioTranscribe -> MedPrepRoutes.AUDIO_TRANSCRIBE
    }
}

/**
 * Extension helper to navigate to SubjectDetail with arguments.
 */
fun NavHostController.navigateToSubjectDetail(subjectId: String) {
    this.navigate(MedPrepRoutes.subjectDetail(subjectId)) {
        launchSingleTop = true
    }
}

/**
 * Extension helper to navigate to Dashboard clearing prior onboarding if needed.
 */
fun NavHostController.navigateToDashboard(clearBackStack: Boolean = false) {
    this.navigate(MedPrepRoutes.DASHBOARD) {
        if (clearBackStack) {
            popUpTo(0) { inclusive = true }
        }
        launchSingleTop = true
    }
}

/**
 * Extension helper to navigate to Onboarding.
 */
fun NavHostController.navigateToOnboarding() {
    this.navigate(MedPrepRoutes.ONBOARDING) {
        launchSingleTop = true
    }
}

/**
 * Core MedPrep AI Navigation Graph connecting Onboarding, Dashboard, SubjectDetail,
 * and collegiate medical learning modules.
 */
@Composable
fun MedPrepNavGraph(
    navController: NavHostController,
    viewModel: MedPrepViewModel,
    startDestination: String = MedPrepRoutes.DASHBOARD,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        // 1. ONBOARDING SCREEN
        // Multi-step academic program selector (MBBS, BDS, B.Pharm, B.Sc Nursing, etc.)
        composable(MedPrepRoutes.ONBOARDING) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigateToDashboard(clearBackStack = true)
                }
            )
        }

        // 2. DASHBOARD SCREEN
        // Central academic cockpit, progress overview, subjects carousel, quick modules
        composable(MedPrepRoutes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    when (screen) {
                        Screen.SubjectDetail -> {
                            val activeSubjId = viewModel.selectedSubject.value?.id ?: "nurs_sem1_anat_phys"
                            navController.navigateToSubjectDetail(activeSubjId)
                        }
                        Screen.Onboarding -> navController.navigateToOnboarding()
                        else -> navController.navigate(screen.toRoute()) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // 3. SUBJECT DETAIL SCREEN (with subjectId parameter)
        // Semester units, chapters, credit breakdown, clinical correlations, and study actions
        composable(
            route = MedPrepRoutes.SUBJECT_DETAIL,
            arguments = listOf(
                navArgument("subjectId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId").orEmpty()
            if (subjectId.isNotBlank() && viewModel.selectedSubject.value?.id != subjectId) {
                viewModel.selectSubjectById(subjectId)
            }

            SubjectDetailScreen(
                viewModel = viewModel,
                onBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(MedPrepRoutes.DASHBOARD)
                    }
                },
                onNavigate = { screen ->
                    navController.navigate(screen.toRoute()) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Direct fallback route for SUBJECT_DETAIL without arguments
        composable(MedPrepRoutes.SUBJECT_DETAIL_BASE) {
            SubjectDetailScreen(
                viewModel = viewModel,
                onBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(MedPrepRoutes.DASHBOARD)
                    }
                },
                onNavigate = { screen ->
                    navController.navigate(screen.toRoute()) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 4. AUTHENTICATION SCREEN
        composable(MedPrepRoutes.AUTH) {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigateToDashboard(clearBackStack = true)
                }
            )
        }

        // 5. SYLLABUS & CURRICULUM BROWSER
        composable(MedPrepRoutes.SYLLABUS) {
            SyllabusScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    when (screen) {
                        Screen.SubjectDetail -> {
                            val activeSubjId = viewModel.selectedSubject.value?.id ?: "nurs_sem1_anat_phys"
                            navController.navigateToSubjectDetail(activeSubjId)
                        }
                        else -> navController.navigate(screen.toRoute()) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // 6. TIMETABLE & STUDY PLANNER
        composable(MedPrepRoutes.TIMETABLE) {
            TimetableScreen(viewModel = viewModel)
        }

        // 7. STUDY NOTES & SUMMARIES
        composable(MedPrepRoutes.NOTES) {
            NotesScreen(viewModel = viewModel)
        }

        // 8. QUESTION BANK & QUIZZES
        composable(MedPrepRoutes.QUIZZES) {
            QuizScreen(viewModel = viewModel)
        }

        // 9. TIMED UNIVERSITY MOCK EXAM
        composable(MedPrepRoutes.MOCK_EXAM) {
            MockExamScreen(viewModel = viewModel)
        }

        // 10. CLINICAL LEARNING & NURSING CARE LAB
        composable(MedPrepRoutes.CLINICAL_LAB) {
            ClinicalLearningScreen(viewModel = viewModel)
        }

        // 11. AI MEDICAL & HEALTHCARE TUTOR
        composable(MedPrepRoutes.AI_TUTOR) {
            AiTutorScreen(viewModel = viewModel)
        }

        // 12. HIGH-YIELD FLASHCARDS
        composable(MedPrepRoutes.FLASHCARDS) {
            FlashcardsScreen(viewModel = viewModel)
        }

        // 13. ACADEMIC ANALYTICS
        composable(MedPrepRoutes.ANALYTICS) {
            AnalyticsScreen(viewModel = viewModel)
        }

        // 14. ADMIN SYLLABUS MANAGEMENT & IMPORT
        composable(MedPrepRoutes.ADMIN_SYLLABUS) {
            AdminSyllabusScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 15. PROFILE & ACADEMIC SETTINGS
        composable(MedPrepRoutes.PROFILE_SETTINGS) {
            ProfileSettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { screen ->
                    if (screen == Screen.Auth) {
                        navController.navigate(MedPrepRoutes.AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else if (screen == Screen.Onboarding) {
                        navController.navigateToOnboarding()
                    } else {
                        navController.navigate(screen.toRoute()) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // 16. AUDIO TRANSCRIPTION (gemini-3.5-transcribe)
        composable(MedPrepRoutes.AUDIO_TRANSCRIBE) {
            AudioTranscriptionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
