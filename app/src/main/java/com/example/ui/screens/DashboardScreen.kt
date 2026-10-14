package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SubjectEntity
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

@Composable
fun DashboardScreen(
    viewModel: MedPrepViewModel,
    onNavigate: (Screen) -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val subjects by viewModel.enrolledSubjects.collectAsState()
    val sessions by viewModel.studySessions.collectAsState()
    val realStreak by viewModel.studyStreakDays.collectAsState()
    val realStudyMinutes by viewModel.totalStudyMinutes.collectAsState()
    val realQuizzesCount by viewModel.quizzesTakenCount.collectAsState()

    val nextSession = sessions.firstOrNull { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Student Academic Header Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedNavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().testTag("student_profile_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(MedNavyDark, MedNavyPrimary)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome, ${profile?.fullName ?: "Healthcare Scholar"}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${profile?.courseName ?: "B.Sc Nursing"} • ${profile?.semesterOrPhase ?: "Semester 1"}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MedTealAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MedTealPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = "Medical Program",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🏛️ ${profile?.collegeName ?: "Government Medical College"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerifiedBadge(isVerified = true)

                        Button(
                            onClick = { onNavigate(Screen.Onboarding) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("switch_course_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Switch Course", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 2. Visual Hero Illustration Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.medprep_hero_banner),
                        contentDescription = "MedPrep AI Clinical Platform",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, MedNavyDark.copy(alpha = 0.85f))
                                )
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = "Professional Healthcare Curriculum • INC & NMC Aligned",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 3. Key Academic Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Study Streak",
                    value = "$realStreak Days",
                    subtitle = if (realStreak > 0) "Active Streak" else "Start streak today",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = if (realStreak > 0) Color(0xFFE65100) else Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Study Logged",
                    value = "${realStudyMinutes / 60}h ${realStudyMinutes % 60}m",
                    subtitle = if (realStudyMinutes > 0) "This Semester" else "No time logged",
                    icon = Icons.Default.Schedule,
                    iconTint = MedTealPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Quizzes Taken",
                    value = "$realQuizzesCount",
                    subtitle = if (realQuizzesCount > 0) "Completed" else "Practice MCQs",
                    icon = Icons.Default.Quiz,
                    iconTint = MedNavyPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Next Study Session Notification Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MedTealPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = MedTealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Next Scheduled Study Session",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = nextSession?.let { "${it.topicName} (${it.startTime})" }
                                    ?: "All current study sessions completed!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (nextSession != null) {
                        TextButton(
                            onClick = { viewModel.toggleSessionCompleted(nextSession) },
                            modifier = Modifier.testTag("mark_session_done_button")
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold, color = MedTealPrimary)
                        }
                    }
                }
            }
        }

        // 5. Quick Action Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Core Learning Modules",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedNavyPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                val quickActions = listOf(
                    QuickAction("Syllabus", Icons.Default.MenuBook, MedNavyPrimary, Screen.Syllabus),
                    QuickAction("Clinical Lab", Icons.Default.LocalHospital, Color(0xFFC2185B), Screen.ClinicalLab),
                    QuickAction("AI Tutor", Icons.Default.Psychology, MedTealPrimary, Screen.AiTutor),
                    QuickAction("Timetable", Icons.Default.CalendarMonth, Color(0xFFE65100), Screen.Timetable),
                    QuickAction("Study Notes", Icons.Default.EditNote, Color(0xFF1565C0), Screen.Notes),
                    QuickAction("Quizzes", Icons.Default.Quiz, Color(0xFF2E7D32), Screen.Quizzes),
                    QuickAction("Mock Exam", Icons.Default.AssignmentLate, Color(0xFF6A1B9A), Screen.MockExam),
                    QuickAction("Flashcards", Icons.Default.Style, Color(0xFFD84315), Screen.Flashcards)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickActions.take(4).forEach { action ->
                        QuickActionButton(action, onNavigate, Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickActions.drop(4).forEach { action ->
                        QuickActionButton(action, onNavigate, Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.AudioTranscribe) }
                        .testTag("voice_dictation_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MedNavyPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MedTealAccent,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Voice Dictation",
                                    tint = MedNavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Clinical Voice Dictation",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MedTealPrimary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "gemini-3.5-transcribe",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Dictate viva answers, ward notes & study flashcards",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // 6. Current Semester Subjects Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enrolled Subjects (${profile?.semesterOrPhase ?: "Sem 1"})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedNavyPrimary
                        )
                    )

                    TextButton(
                        onClick = { onNavigate(Screen.Syllabus) },
                        modifier = Modifier.testTag("view_all_subjects_button")
                    ) {
                        Text("View All", color = MedTealPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                if (subjects.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading curriculum subjects for your program...")
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(subjects) { subject ->
                            SubjectProgressCard(
                                subject = subject,
                                onClick = {
                                    viewModel.selectSubject(subject)
                                    onNavigate(Screen.SubjectDetail)
                                }
                            )
                        }
                    }
                }
            }
        }

        // 7. Clinical High-Yield Pearl of the Day
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FDF4)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF86EFAC), Color(0xFF4ADE80)))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Clinical & Exam High-Yield Pearl",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Rule of Nines for Burns: Head & Neck = 9%, Each Upper Limb = 9%, Anterior Trunk = 18%, Posterior Trunk = 18%, Each Lower Limb = 18%, Perineum = 1%.\n• Parkland Fluid Formula: 4 mL × Total Body Weight (kg) × % TBSA burned, with first 50% administered in the initial 8 hours.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF14532D),
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

private data class QuickAction(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val screen: Screen
)

@Composable
private fun QuickActionButton(
    action: QuickAction,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clickable { onNavigate(action.screen) }
            .testTag("action_${action.screen.route}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = action.color.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.name,
                        tint = action.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = action.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubjectProgressCard(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subject.code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MedTealPrimary
                )
                Text(
                    text = "${subject.credits} Credits",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subject.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { subject.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MedTealPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${subject.theoryHours}h Theory • ${subject.practicalHours}h Pract",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${subject.progressPercentage}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MedTealPrimary
                )
            }
        }
    }
}
