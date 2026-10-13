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
import com.example.data.local.SeedData
import com.example.data.model.SubjectEntity
import com.example.data.model.UnitEntity
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSyllabusScreen(
    viewModel: MedPrepViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val allSubjects by viewModel.allCourseSubjects.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showImportPackageDialog by remember { mutableStateOf(false) }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Curriculum Administration & Import", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedNavyPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSubjectDialog = true },
                containerColor = MedTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_add_subject_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
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
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Regulatory Curriculum Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MedNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Administer verified academic subjects, units, credit hours, and assessment structures for ${profile?.courseName ?: "Active Program"}.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showImportPackageDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                                modifier = Modifier.testTag("open_import_package_button")
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Official Syllabus Package", fontSize = 11.sp)
                            }
                        }

                        importStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "✅ $msg", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catalogued Subjects in Active Program (${allSubjects.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedNavyPrimary
                    )

                    TextButton(onClick = { showAddSubjectDialog = true }) {
                        Text("+ Add Single Subject", fontSize = 11.sp, color = MedTealPrimary)
                    }
                }
            }

            items(allSubjects) { subj ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${subj.code} • ${subj.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${subj.semesterOrPhase} • ${subj.credits} Credits • ${subj.theoryHours}h Th / ${subj.practicalHours}h Pr",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        VerifiedBadge(isVerified = subj.isVerified)
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AddCustomSubjectDialog(
            currentSemester = profile?.semesterOrPhase ?: "Semester 1",
            onDismiss = { showAddSubjectDialog = false },
            onSave = { name, code, cred, th, pr, cl, sem, obj, assess, verified ->
                viewModel.addCustomSubject(name, code, cred, th, pr, cl, sem, obj, assess, verified)
                showAddSubjectDialog = false
            }
        )
    }

    if (showImportPackageDialog) {
        ImportSyllabusPackageDialog(
            onDismiss = { showImportPackageDialog = false },
            onImport = { subjects, units, label ->
                viewModel.importSyllabusBatch(subjects, units) { success, msg ->
                    importStatusMessage = if (success) "Package '$label' successfully committed to database." else msg
                }
                showImportPackageDialog = false
            }
        )
    }
}

@Composable
fun ImportSyllabusPackageDialog(
    onDismiss: () -> Unit,
    onImport: (List<SubjectEntity>, List<UnitEntity>, String) -> Unit
) {
    val presets = listOf(
        "INC B.Sc Nursing Revised CBME Standard (Semesters 1-8)" to ("bsc_nursing" to listOf(
            SubjectEntity("nurs_ext_res", "bsc_nursing", "4th Year", "Semester 7", "Nursing Research & Statistics", "N-RES-410", 4, 60, 20, 0, "Scientific research method, qualitative/quantitative study designs, sampling, data analysis, and evidence-based practice.", "Internal: 25 marks, University Theory: 75 marks", true),
            SubjectEntity("nurs_ext_mgmt", "bsc_nursing", "4th Year", "Semester 8", "Nursing Management & Leadership", "N-MGT-415", 4, 60, 0, 80, "Principles of management, hospital ward administration, staffing ratios, budgeting, quality assurance, and NABH accreditation standards.", "Internal: 25 marks, University Theory: 75 marks", true)
        )),
        "NMC MBBS Competency-Based Model (Pre-Clinical & Clinical)" to ("mbbs" to listOf(
            SubjectEntity("mbbs_ext_derma", "mbbs", "Final Professional", "Phase 3 Part 2 (Final Prof)", "Dermatology, Venereology & Leprology", "DERM-402", 4, 30, 0, 60, "Clinical dermatology, infectious skin lesions, autoimmune bullous diseases, STIs, and Hansen's disease management.", "Internal: 25 marks, Integrated Theory: 50 marks", true),
            SubjectEntity("mbbs_ext_psych", "mbbs", "Final Professional", "Phase 3 Part 2 (Final Prof)", "Psychiatry & Behavioral Health", "PSY-403", 4, 30, 0, 60, "Clinical diagnosis of mood disorders, schizophrenia, substance abuse, and psychopharmacology.", "Internal: 25 marks, Integrated Theory: 50 marks", true)
        )),
        "PCI B.Pharmacy Standard Regulations Package" to ("b_pharm" to listOf(
            SubjectEntity("bpharm_ext_phchem", "b_pharm", "1st Year", "Semester 2", "Pharmaceutical Organic Chemistry I", "BP202T", 4, 45, 30, 0, "Alkanes, alkenes, conjugated dienes, aliphatic amines, and nucleophilic substitution mechanisms.", "Internal: 25 marks, Exam: 75 marks", true),
            SubjectEntity("bpharm_ext_patho", "b_pharm", "1st Year", "Semester 2", "Pathophysiology", "BP204T", 4, 45, 0, 0, "Basic principles of cell injury and adaptation, cardiovascular disorders, and endocrine pathophysiology.", "Internal: 25 marks, Exam: 75 marks", true)
        ))
    )

    var selectedPresetIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Official Curriculum Importer", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select verified regulatory syllabus package to merge into your institutional database:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                presets.forEachIndexed { index, (label, _) ->
                    val isSelected = selectedPresetIndex == index
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MedTealPrimary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MedTealPrimary)
                        ) else CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPresetIndex = index }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = { selectedPresetIndex = index })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (label, pair) = presets[selectedPresetIndex]
                    val (_, subjects) = pair
                    onImport(subjects, emptyList(), label)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.testTag("confirm_import_package_button")
            ) {
                Text("Import Package")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCustomSubjectDialog(
    currentSemester: String,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int, Int, Int, String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var credits by remember { mutableIntStateOf(4) }
    var theoryHours by remember { mutableIntStateOf(60) }
    var practicalHours by remember { mutableIntStateOf(30) }
    var clinicalHours by remember { mutableIntStateOf(0) }
    var semester by remember { mutableStateOf(currentSemester) }
    var objectives by remember { mutableStateOf("Core learning competencies prescribed by curriculum committee.") }
    var assessment by remember { mutableStateOf("Internal: 25 marks, University Exam: 75 marks") }
    var isVerified by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Curriculum Subject", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth().testTag("admin_subj_name")) }
                item { OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Official Subject Code") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = semester, onValueChange = { semester = it }, label = { Text("Semester / Phase") }, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = credits.toString(), onValueChange = { credits = it.toIntOrNull() ?: 4 }, label = { Text("Credits") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = theoryHours.toString(), onValueChange = { theoryHours = it.toIntOrNull() ?: 60 }, label = { Text("Theory (h)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = practicalHours.toString(), onValueChange = { practicalHours = it.toIntOrNull() ?: 30 }, label = { Text("Pract (h)") }, modifier = Modifier.weight(1f))
                    }
                }
                item { OutlinedTextField(value = objectives, onValueChange = { objectives = it }, label = { Text("Learning Objectives") }, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isVerified, onCheckedChange = { isVerified = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark as Verified Official Curriculum", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, code, credits, theoryHours, practicalHours, clinicalHours, semester, objectives, assessment, isVerified)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.testTag("admin_save_subject_button")
            ) {
                Text("Publish to Syllabus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
