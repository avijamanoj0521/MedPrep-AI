package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChapterEntity
import com.example.data.model.UnitEntity
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

@Composable
fun SubjectDetailScreen(
    viewModel: MedPrepViewModel,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val subject by viewModel.selectedSubject.collectAsState()
    val units by viewModel.unitsForSelectedSubject.collectAsState()

    if (subject == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No subject selected")
        }
        return
    }

    Scaffold(
        topBar = {
            Surface(color = MedNavyPrimary, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject!!.code,
                                color = MedTealAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = subject!!.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }
                        VerifiedBadge(isVerified = subject!!.isVerified)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject Academic Overview
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Prescribed Learning Objectives",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MedNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subject!!.learningObjectives,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Examination & Assessment Scheme",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MedNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subject!!.assessmentPattern,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedTealPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigate(Screen.AiTutor) },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("ask_ai_subject_button")
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ask AI Tutor", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Screen.Quizzes) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("quiz_subject_button")
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Practice MCQs", fontSize = 12.sp)
                    }
                }
            }

            // Units Header
            item {
                Text(
                    text = "Prescribed Curriculum Units (${units.size} Units Available):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MedNavyPrimary
                )
            }

            if (units.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Units currently being compiled for this subject.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { onNavigate(Screen.AdminSyllabus) }) {
                                Text("Import Units via Syllabus Manager", color = MedTealPrimary)
                            }
                        }
                    }
                }
            } else {
                items(units) { unit ->
                    UnitExpandableCard(
                        unit = unit,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun UnitExpandableCard(
    unit: UnitEntity,
    viewModel: MedPrepViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val chaptersFlow = remember(unit.id) { viewModel.getChaptersForUnit(unit.id) }
    val chapters by chaptersFlow.collectAsState(initial = emptyList())

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unit_card_${unit.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNIT ${unit.unitNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MedTealPrimary
                    )
                    Text(
                        text = unit.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = unit.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Unit Chapters"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Unit Chapters & High-Yield Revisions:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MedNavyDark
                )

                if (chapters.isEmpty()) {
                    Text(
                        text = "Detailed chapter breakdown available in syllabus manager.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    chapters.forEach { chapter ->
                        ChapterRowItem(
                            chapter = chapter,
                            onToggleComplete = { viewModel.toggleChapterCompletion(chapter) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterRowItem(
    chapter: ChapterEntity,
    onToggleComplete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (chapter.isCompleted) Color(0xFFF0FDF4)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = chapter.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    modifier = Modifier.testTag("ch_checkbox_${chapter.id}")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${chapter.chapterNumber}. ${chapter.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (chapter.isCompleted) Color(0xFF166534)
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (chapter.isCompleted) "Completed & Revised" else "Pending Study",
                        fontSize = 10.sp,
                        color = if (chapter.isCompleted) Color(0xFF16A34A)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (chapter.highYieldSummary.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 42.dp, top = 4.dp)
                ) {
                    Text(
                        text = "💡 ${chapter.highYieldSummary}",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
