package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.MedPrepBottomBar
import com.example.ui.components.MedPrepTopBar
import com.example.ui.navigation.MedPrepNavGraph
import com.example.ui.navigation.MedPrepRoutes
import com.example.ui.navigation.toRoute
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MedPrepApp()
            }
        }
    }
}

@Composable
fun MedPrepApp(
    viewModel: MedPrepViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MedPrepRoutes.DASHBOARD

    // Determine current active screen for top and bottom navigation bars
    val currentScreen = when {
        currentRoute.startsWith("onboarding") -> Screen.Onboarding
        currentRoute.startsWith("auth") -> Screen.Auth
        currentRoute.startsWith("subject_detail") -> Screen.SubjectDetail
        currentRoute.startsWith("syllabus") -> Screen.Syllabus
        currentRoute.startsWith("timetable") -> Screen.Timetable
        currentRoute.startsWith("notes") -> Screen.Notes
        currentRoute.startsWith("quizzes") -> Screen.Quizzes
        currentRoute.startsWith("mock_exam") -> Screen.MockExam
        currentRoute.startsWith("clinical_lab") -> Screen.ClinicalLab
        currentRoute.startsWith("ai_tutor") -> Screen.AiTutor
        currentRoute.startsWith("flashcards") -> Screen.Flashcards
        currentRoute.startsWith("analytics") -> Screen.Analytics
        currentRoute.startsWith("admin_syllabus") -> Screen.AdminSyllabus
        currentRoute.startsWith("profile_settings") -> Screen.ProfileSettings
        currentRoute.startsWith("audio_transcribe") -> Screen.AudioTranscribe
        else -> Screen.Dashboard
    }

    val showBars = currentScreen !in listOf(
        Screen.Auth,
        Screen.Onboarding,
        Screen.SubjectDetail,
        Screen.AdminSyllabus,
        Screen.ProfileSettings,
        Screen.AudioTranscribe
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showBars) {
                MedPrepTopBar(
                    currentScreen = currentScreen,
                    onProfileClick = { navController.navigate(MedPrepRoutes.PROFILE_SETTINGS) },
                    onAdminClick = { navController.navigate(MedPrepRoutes.ADMIN_SYLLABUS) }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                MedPrepBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        navController.navigate(screen.toRoute()) {
                            popUpTo(MedPrepRoutes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MedPrepNavGraph(
                navController = navController,
                viewModel = viewModel,
                startDestination = MedPrepRoutes.DASHBOARD,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
