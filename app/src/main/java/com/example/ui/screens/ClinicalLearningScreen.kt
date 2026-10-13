package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.ClinicalCaseEntity
import com.example.data.model.NursingCarePlanEntity
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalLearningScreen(viewModel: MedPrepViewModel) {
    val clinicalCases by viewModel.clinicalCases.collectAsState()
    val carePlans by viewModel.nursingCarePlans.collectAsState()
    val wardLogs by viewModel.wardLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Care Plans, 1: Clinical Cases, 2: Drug Classes, 3: Ward Logs & OSPE
    var showCreateNcpDialog by remember { mutableStateOf(false) }
    var showAddWardLogDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showCreateNcpDialog = true },
                    containerColor = MedTealPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_ncp_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Nursing Care Plan")
                }
            } else if (selectedTab == 3) {
                FloatingActionButton(
                    onClick = { showAddWardLogDialog = true },
                    containerColor = MedNavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_ward_log_fab")
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = "Log Ward Posting")
                }
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
            Surface(color = MedNavyPrimary, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Learning & Nursing Care Lab",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "NANDA Care Plans • Case Simulations • Ward Posting Log • OSPE",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedTealAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedNavyPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Care Plans", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Case Simulations", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Drug Classes", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Ward Log & OSPE", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> CarePlansTab(carePlans = carePlans, onDelete = { viewModel.deleteCarePlan(it) })
                1 -> ClinicalCasesTab(cases = clinicalCases)
                2 -> DrugClassesTab()
                3 -> WardLogsAndProceduresTab(
                    wardLogs = wardLogs,
                    onDeleteLog = { viewModel.deleteWardLog(it) }
                )
            }
        }
    }

    if (showAddWardLogDialog) {
        AddWardLogDialog(
            onDismiss = { showAddWardLogDialog = false },
            onSave = { ward, date, caseType, procs, sup, ref, hrs ->
                viewModel.addWardLog(ward, date, caseType, procs, sup, ref, hrs)
                showAddWardLogDialog = false
            }
        )
    }

    if (showCreateNcpDialog) {
        CreateCarePlanDialog(
            onDismiss = { showCreateNcpDialog = false },
            onSave = { initials, diag, sub, obj, nanda, goal, inter, rat, eval ->
                viewModel.saveNursingCarePlan(initials, diag, sub, obj, nanda, goal, inter, rat, eval)
                showCreateNcpDialog = false
            }
        )
    }
}

@Composable
fun CarePlansTab(
    carePlans: List<NursingCarePlanEntity>,
    onDelete: (NursingCarePlanEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MedNavyPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Standard ADPIE format (Assessment, Diagnosis, Planning, Implementation, Evaluation). Tap '+' to author a clinical care plan.",
                        fontSize = 11.sp,
                        color = MedNavyDark,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        if (carePlans.isEmpty()) {
            item {
                Text(
                    text = "No saved care plans yet. Tap '+' to create your first Nursing Care Plan.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(carePlans) { ncp ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ncp_card_${ncp.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Patient: ${ncp.patientInitials} • ${ncp.medicalDiagnosis}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MedNavyPrimary
                            )
                            IconButton(onClick = { onDelete(ncp) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NANDA Diagnosis: ${ncp.nandaDiagnosis}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MedTealPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "SMART Goal: ${ncp.smartGoal}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Key Interventions & Rationales:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(ncp.nursingInterventions, fontSize = 11.sp, lineHeight = 15.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Evaluation: ${ncp.evaluation}", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicalCasesTab(cases: List<ClinicalCaseEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Educational Simulation Only: Real clinical procedures require direct qualified preceptor supervision and patient consent.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        items(cases) { c ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("case_sim_${c.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = c.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MedNavyPrimary
                    )
                    Text(
                        text = "Patient: ${c.patientAgeGender}",
                        fontSize = 12.sp,
                        color = MedTealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Chief Complaint: ${c.chiefComplaint}",
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📊 Vital Signs: ${c.vitalSigns}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedNavyDark,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Priority Clinical Interventions:\n${c.priorityNursingInterventions}",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DrugClassesTab() {
    val drugs = listOf(
        Triple("Beta Blockers (Metoprolol, Atenolol)", "Blocks beta-1 receptors -> Decr. HR, decr. myocardial contractility.", "Nursing: Check apical pulse before administering. Withhold if HR < 60 bpm or SBP < 100 mmHg. Caution in asthma/COPD."),
        Triple("ACE Inhibitors (Enalapril, Ramipril)", "Inhibits ACE -> Decr. Angiotensin II & Aldosterone, vasodilation.", "Nursing: Monitor for hyperkalemia and dry cough. Instruct patient to rise slowly (orthostatic hypotension risk)."),
        Triple("Loop Diuretics (Furosemide / Lasix)", "Inhibits Na+/K+/2Cl- symporter in thick ascending limb of Henle.", "Nursing: Monitor serum Potassium (risk of hypokalemia) and intake/output. Administer morning to avoid nocturia."),
        Triple("Glycopeptide Antibiotics (Vancomycin)", "Inhibits cell wall synthesis in Gram-positive bacteria (MRSA).", "Nursing: Infuse over at least 60 minutes to prevent 'Red Man Syndrome'. Monitor peak and trough serum levels.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(drugs) { (name, moa, nursing) ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedNavyPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Mechanism: $moa", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Clinical Pearls: $nursing", fontSize = 11.sp, color = MedTealPrimary, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
fun WardLogsAndProceduresTab(
    wardLogs: List<com.example.data.model.ClinicalWardLogEntity>,
    onDeleteLog: (com.example.data.model.ClinicalWardLogEntity) -> Unit
) {
    val procedures = listOf(
        "Peripheral IV Cannulation OSPE Checklist" to listOf("1. Verify physician order & patient identity (2 identifiers)", "2. Explain procedure and obtain informed consent", "3. Perform hand hygiene (WHO Moment 2) & assemble equipment", "4. Apply tourniquet 10-15 cm above venipuncture site", "5. Clean site with 2% chlorhexidine in 70% alcohol for 30s; let air dry", "6. Insert cannula at 15-30 degree angle bevel up; confirm flashback", "7. Lower angle, advance catheter, release tourniquet, secure with sterile dressing"),
        "Adult Basic Life Support (BLS) Algorithm" to listOf("1. Ensure scene safety", "2. Check responsiveness: tap and shout", "3. Shout for nearby help and activate Emergency Medical Services (EMS)", "4. Check carotid pulse and breathing simultaneously for 5-10 seconds", "5. If no pulse, start CPR: 30 compressions to 2 breaths (Rate 100-120/min, Depth 5-6 cm)", "6. Apply AED as soon as available and follow prompts")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clinical Ward Posting Logbook (${wardLogs.size} Postings)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MedNavyPrimary
                )
            }
        }

        if (wardLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                        Text("No ward clinical postings logged yet. Tap '+' to log a posting.", fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(wardLogs) { log ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ward_log_${log.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(log.wardName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedNavyPrimary)
                            IconButton(onClick = { onDeleteLog(log) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text("${log.postingDate} • ${log.hoursLogged} Hours Logged", fontSize = 11.sp, color = MedTealPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Case / Condition: ${log.patientCaseType}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Procedures Observed / Assisted: ${log.proceduresAssisted}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Supervisor: ${log.supervisorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💡 Reflection: ${log.clinicalReflection}", fontSize = 11.sp, modifier = Modifier.padding(8.dp), lineHeight = 15.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Core Clinical OSPE Procedures:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedNavyPrimary)
        }

        items(procedures) { (title, steps) ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedNavyPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    steps.forEach { step ->
                        Text(step, fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddWardLogDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, Int) -> Unit
) {
    var wardName by remember { mutableStateOf("Intensive Care Unit (ICU)") }
    var postingDate by remember { mutableStateOf("Today") }
    var caseType by remember { mutableStateOf("Acute Respiratory Distress Syndrome (ARDS)") }
    var procedures by remember { mutableStateOf("Assisted with endotracheal tube suctioning & ventilator circuit check") }
    var supervisor by remember { mutableStateOf("Sister Incharge Rekha") }
    var reflection by remember { mutableStateOf("Learned sterile in-line suctioning protocol and preventing oxygen desaturation.") }
    var hours by remember { mutableIntStateOf(6) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Ward Clinical Posting", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = wardName, onValueChange = { wardName = it }, label = { Text("Ward / Clinical Posting Unit") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = postingDate, onValueChange = { postingDate = it }, label = { Text("Date / Shift") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = caseType, onValueChange = { caseType = it }, label = { Text("Patient Clinical Presentation / Case") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = procedures, onValueChange = { procedures = it }, label = { Text("Procedures Observed / Assisted") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = supervisor, onValueChange = { supervisor = it }, label = { Text("Clinical Preceptor / Supervisor") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = reflection, onValueChange = { reflection = it }, label = { Text("Clinical Reflection / Learnings") }, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(wardName, postingDate, caseType, procedures, supervisor, reflection, hours) },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
            ) {
                Text("Save to Logbook")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateCarePlanDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    var patientInitials by remember { mutableStateOf("M.S.") }
    var diagnosis by remember { mutableStateOf("Acute Bronchial Asthma Exacerbation") }
    var subjective by remember { mutableStateOf("Patient states: 'I cannot catch my breath, chest feels tight.'") }
    var objective by remember { mutableStateOf("SpO2 89%, RR 28/min, audible expiratory wheezing.") }
    var nanda by remember { mutableStateOf("Ineffective Airway Clearance related to bronchospasm.") }
    var goal by remember { mutableStateOf("Patient will maintain SpO2 >= 95% on room air within 2 hours.") }
    var interventions by remember { mutableStateOf("Position in High-Fowler's; administer prescribed nebulized Salbutamol and supplemental O2.") }
    var rationales by remember { mutableStateOf("High-Fowler position maximizes lung expansion and decreases work of breathing.") }
    var evaluation by remember { mutableStateOf("Goal met: SpO2 improved to 97%, respiratory rate reduced to 18/min.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Author Nursing Care Plan (ADPIE)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.height(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Medical Diagnosis") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = nanda, onValueChange = { nanda = it }, label = { Text("NANDA-I Nursing Diagnosis") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = subjective, onValueChange = { subjective = it }, label = { Text("Subjective Data") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = objective, onValueChange = { objective = it }, label = { Text("Objective Data") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("SMART Goal") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = interventions, onValueChange = { interventions = it }, label = { Text("Interventions") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = rationales, onValueChange = { rationales = it }, label = { Text("Rationales") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = evaluation, onValueChange = { evaluation = it }, label = { Text("Evaluation") }, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(patientInitials, diagnosis, subjective, objective, nanda, goal, interventions, rationales, evaluation) },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
            ) {
                Text("Save Care Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
