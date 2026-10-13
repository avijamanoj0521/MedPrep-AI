package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@Composable
fun AnalyticsScreen(viewModel: MedPrepViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val subjects by viewModel.enrolledSubjects.collectAsState()
    val quizAttempts by viewModel.quizAttempts.collectAsState()
    val sessions by viewModel.studySessions.collectAsState()

    val completedSessions = sessions.count { it.isCompleted }
    val totalQuizzes = quizAttempts.size
    val averageScore = if (totalQuizzes > 0) {
        val totalScore = quizAttempts.sumOf { it.score }
        val totalQuestions = quizAttempts.sumOf { it.totalQuestions }
        if (totalQuestions > 0) (totalScore * 100) / totalQuestions else 0
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Surface(
                color = MedNavyPrimary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Academic & Clinical Analytics",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Real-time performance tracking across coursework, assessments & clinical prep",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedTealAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Avg MCQ Accuracy",
                    value = "$averageScore%",
                    icon = Icons.Default.GpsFixed,
                    color = MedTealPrimary,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Sessions Done",
                    value = "$completedSessions",
                    icon = Icons.Default.TaskAlt,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Study Streak",
                    value = "${profile?.studyStreakDays ?: 5} Days",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subject Progress Bars
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Syllabus Mastery (${profile?.semesterOrPhase ?: "Current Period"})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (subjects.isEmpty()) {
                        Text("No enrolled subjects to track.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        subjects.forEach { subject ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = subject.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${subject.progressPercentage}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MedTealPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { subject.progressPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MedTealPrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Milestone Achievements
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Academic Milestones & Badges",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val badges = listOf(
                        Triple("Clinical Scholar", "Logged over 180 minutes of verified study", Icons.Default.WorkspacePremium),
                        Triple("Care Plan Champion", "Created educational ADPIE nursing care plan", Icons.Default.Stars),
                        Triple("Diagnostic Thinker", "Completed acute clinical case simulation", Icons.Default.Psychology)
                    )

                    badges.forEach { (name, desc, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MedNavyPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = icon, contentDescription = null, tint = MedNavyPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
