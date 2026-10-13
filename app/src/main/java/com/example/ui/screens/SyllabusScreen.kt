package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SeedData
import com.example.data.model.SubjectEntity
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

@Composable
fun SyllabusScreen(
    viewModel: MedPrepViewModel,
    onNavigate: (Screen) -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val subjects by viewModel.enrolledSubjects.collectAsState()
    val allSubjects by viewModel.allCourseSubjects.collectAsState()

    val currentProgram = SeedData.supportedCourses.find { it.id == profile?.courseId }
        ?: SeedData.supportedCourses.first()

    var searchQuery by remember { mutableStateOf("") }

    val filteredSubjects = if (searchQuery.isBlank()) {
        subjects
    } else {
        allSubjects.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("syllabus_screen")
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Course & Regulatory Banner
        Surface(
            color = MedNavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = currentProgram.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${currentProgram.regulatoryBody} • ${profile?.curriculumVersion ?: "Official Structure"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MedTealAccent,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search subjects by name or code (e.g. Anatomy, NF-110)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("syllabus_search_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Semester / Professional Phase Selector Tabs
        if (searchQuery.isBlank()) {
            ScrollableTabRow(
                selectedTabIndex = currentProgram.semestersOrPhases.indexOf(selectedSemester).coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedNavyPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                currentProgram.semestersOrPhases.forEach { semester ->
                    val isSelected = semester == selectedSemester
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectSemester(semester) },
                        text = {
                            Text(
                                text = semester,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MedNavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("sem_tab_$semester")
                    )
                }
            }
        }

        // Subject Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Search Results (${filteredSubjects.size})"
                        else "Prescribed Subjects for $selectedSemester (${filteredSubjects.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(onClick = { onNavigate(Screen.AdminSyllabus) }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Subject", fontSize = 11.sp, color = MedTealPrimary)
                    }
                }
            }

            if (filteredSubjects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MedTealPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Syllabus Not Yet Imported for $selectedSemester",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "MedPrep AI allows administrators to import curriculum via JSON/CSV, or you can add custom subjects anytime.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onNavigate(Screen.AdminSyllabus) },
                                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
                            ) {
                                Text("Import / Add Subject")
                            }
                        }
                    }
                }
            } else {
                items(filteredSubjects) { subject ->
                    SyllabusSubjectCard(
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

@Composable
fun SyllabusSubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("syllabus_subject_${subject.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MedNavyPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = subject.code,
                            color = MedNavyPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${subject.credits} Credits",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerifiedBadge(isVerified = subject.isVerified)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subject.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subject.learningObjectives,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hours Distribution Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HourChip("Theory: ${subject.theoryHours}h", Icons.Default.MenuBook)
                HourChip("Practical: ${subject.practicalHours}h", Icons.Default.Science)
                if (subject.clinicalHours > 0) {
                    HourChip("Clinical: ${subject.clinicalHours}h", Icons.Default.LocalHospital)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LinearProgressIndicator(
                    progress = { subject.progressPercentage / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MedTealPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${subject.progressPercentage}% Complete",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MedTealPrimary
                )
            }
        }
    }
}

@Composable
private fun HourChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
