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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySessionEntity
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(viewModel: MedPrepViewModel) {
    val sessions by viewModel.studySessions.collectAsState()
    val enrolledSubjects by viewModel.enrolledSubjects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Today", "Pending", "Completed"

    val filteredSessions = when (selectedFilter) {
        "Today" -> sessions.filter { it.dateString.equals("Today", ignoreCase = true) }
        "Pending" -> sessions.filter { !it.isCompleted }
        "Completed" -> sessions.filter { it.isCompleted }
        else -> sessions
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MedTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_session_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Schedule Study Session")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Surface(
                color = MedNavyPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical & Academic Study Planner",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Plan theory lectures, clinical ward postings, practicals & revision",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedTealAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Today", "Completed").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) }
                    )
                }
            }

            // Sessions List
            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = MedTealPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No study sessions found in this filter.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the '+' button to schedule your next lecture, ward posting or revision.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSessions, key = { it.id }) { session ->
                        StudySessionCard(
                            session = session,
                            onToggleComplete = { viewModel.toggleSessionCompleted(session) },
                            onDelete = { viewModel.deleteStudySession(session) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStudySessionDialog(
            subjectNames = enrolledSubjects.map { it.name }.ifEmpty {
                listOf("Applied Anatomy & Physiology", "Nursing Foundation I", "Biochemistry", "Pathology", "General Medicine")
            },
            onDismiss = { showAddDialog = false },
            onSave = { subject, topic, date, time, duration, type ->
                viewModel.addStudySession(subject, topic, date, time, duration, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StudySessionCard(
    session: StudySessionEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (session.isCompleted) Color(0xFFF8FAFC)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (session.isCompleted) 1.dp else 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_card_${session.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.testTag("check_session_${session.id}")
            ) {
                Icon(
                    imageVector = if (session.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Session Completion",
                    tint = if (session.isCompleted) Color(0xFF16A34A) else MedTealPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (session.sessionType) {
                            "Clinical Posting" -> Color(0xFFFCE4EC)
                            "Practical" -> Color(0xFFE8F5E9)
                            "Revision" -> Color(0xFFFFF3E0)
                            else -> MedNavyPrimary.copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            text = session.sessionType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (session.sessionType) {
                                "Clinical Posting" -> Color(0xFFC2185B)
                                "Practical" -> Color(0xFF2E7D32)
                                "Revision" -> Color(0xFFE65100)
                                else -> MedNavyPrimary
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "${session.dateString} • ${session.startTime} (${session.durationMinutes} min)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = session.topicName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (session.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = session.subjectName,
                    fontSize = 12.sp,
                    color = MedTealPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Session",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddStudySessionDialog(
    subjectNames: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(subjectNames.firstOrNull() ?: "") }
    var topicName by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("Today") }
    var startTime by remember { mutableStateOf("05:00 PM") }
    var durationMinutes by remember { mutableIntStateOf(60) }
    var sessionType by remember { mutableStateOf("Theory") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Study Session", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = topicName,
                    onValueChange = { topicName = it },
                    label = { Text("Topic / Chapter to Study") },
                    placeholder = { Text("e.g., Cardiac Conduction System") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_topic_input")
                )

                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = { selectedSubject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Day / Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Duration:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    listOf(30, 60, 90, 120).forEach { mins ->
                        FilterChip(
                            selected = durationMinutes == mins,
                            onClick = { durationMinutes = mins },
                            label = { Text("${mins}m", fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Theory", "Practical", "Clinical Posting", "Revision").forEach { type ->
                        FilterChip(
                            selected = sessionType == type,
                            onClick = { sessionType = type },
                            label = { Text(type, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topicName.isNotBlank()) {
                        onSave(selectedSubject, topicName, dateString, startTime, durationMinutes, sessionType)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.testTag("dialog_save_session_button")
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
