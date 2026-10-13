package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.local.SeedData
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: MedPrepViewModel,
    onComplete: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()

    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "Priya Sharma") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "priya.nursing@medprep.edu") }
    var selectedCourseId by remember(profile) { mutableStateOf(profile?.courseId ?: "bsc_nursing") }
    var university by remember(profile) { mutableStateOf(profile?.university ?: "Rajiv Gandhi University of Health Sciences") }
    var collegeName by remember(profile) { mutableStateOf(profile?.collegeName ?: "Government College of Nursing, Bengaluru") }
    var admissionBatch by remember(profile) { mutableStateOf(profile?.admissionBatch ?: "2023-2027 Batch") }
    var academicYear by remember(profile) { mutableStateOf(profile?.academicYear ?: "1st Year") }
    var semesterOrPhase by remember(profile) { mutableStateOf(profile?.semesterOrPhase ?: "Semester 1") }
    var curriculumVersion by remember(profile) { mutableStateOf(profile?.curriculumVersion ?: "INC Revised CBME Curriculum 2021") }

    var step by remember { mutableIntStateOf(1) } // 1: Program, 2: Institution & Batch, 3: Semester/Phase

    val currentProgram = SeedData.supportedCourses.find { it.id == selectedCourseId }
        ?: SeedData.supportedCourses.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Student Academic Onboarding",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (step > 1) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedNavyPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Step Progress Indicator
            LinearProgressIndicator(
                progress = { step / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MedTealAccent,
                trackColor = MedNavyDark
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = when (step) {
                            1 -> "Step 1: Select Your Healthcare Program"
                            2 -> "Step 2: University & College Affiliation"
                            else -> "Step 3: Academic Phase, Semester & Curriculum"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedNavyPrimary
                        )
                    )
                    Text(
                        text = "MedPrep AI customizes your subjects, clinical simulations, and question bank according to your regulatory curriculum.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (step == 1) {
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Student Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_name_input"),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("College Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_email_input"),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                        )
                    }

                    item {
                        Text(
                            text = "Select Healthcare Degree Program (${SeedData.supportedCourses.size} Available):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MedNavyPrimary
                        )
                    }

                    items(SeedData.supportedCourses) { course ->
                        val isSelected = course.id == selectedCourseId
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MedTealPrimary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MedTealPrimary)
                            ) else CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCourseId = course.id
                                    semesterOrPhase = course.semestersOrPhases.firstOrNull() ?: "Semester 1"
                                }
                                .testTag("course_option_${course.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedCourseId = course.id
                                        semesterOrPhase = course.semestersOrPhases.firstOrNull() ?: "Semester 1"
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = course.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Regulated by: ${course.regulatoryBody}",
                                        fontSize = 11.sp,
                                        color = MedTealPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = course.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                } else if (step == 2) {
                    item {
                        OutlinedTextField(
                            value = university,
                            onValueChange = { university = it },
                            label = { Text("University / Examining Body") },
                            placeholder = { Text("e.g. Rajiv Gandhi University of Health Sciences, MUHS, WBUHS") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("university_input"),
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = collegeName,
                            onValueChange = { collegeName = it },
                            label = { Text("College / Institute Name") },
                            placeholder = { Text("e.g. Govt Medical/Nursing College") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("college_input"),
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = admissionBatch,
                            onValueChange = { admissionBatch = it },
                            label = { Text("Admission Batch") },
                            placeholder = { Text("e.g. 2023-2027 Batch") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("batch_input"),
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "Current Academic Period for ${currentProgram.shortName}:",
                            fontWeight = FontWeight.Bold,
                            color = MedNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        currentProgram.semestersOrPhases.forEach { sem ->
                            val isSelected = sem == semesterOrPhase
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MedTealPrimary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(MedTealPrimary)
                                ) else CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { semesterOrPhase = sem }
                                    .testTag("sem_option_$sem")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { semesterOrPhase = sem }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = sem,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = curriculumVersion,
                            onValueChange = { curriculumVersion = it },
                            label = { Text("Curriculum Regulation Version") },
                            placeholder = { Text("e.g. INC Revised Regulations 2021 / NMC CBME 2023") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("curriculum_version_input")
                        )
                    }
                }
            }

            // Bottom Navigation Controls
            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step++
                            } else {
                                viewModel.updateProfile(
                                    fullName = fullName,
                                    email = email,
                                    courseId = selectedCourseId,
                                    courseName = currentProgram.name,
                                    university = university,
                                    college = collegeName,
                                    admissionBatch = admissionBatch,
                                    academicYear = academicYear,
                                    semesterOrPhase = semesterOrPhase,
                                    curriculumVersion = curriculumVersion
                                )
                                onComplete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_continue_button")
                    ) {
                        Text(if (step < 3) "Next Step" else "Save & Launch Dashboard")
                    }
                }
            }
        }
    }
}
