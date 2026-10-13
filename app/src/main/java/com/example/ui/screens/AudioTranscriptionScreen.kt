package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ai.AudioTranscribeService
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioTranscriptionScreen(
    viewModel: MedPrepViewModel,
    onBack: () -> Unit = { viewModel.navigateTo(Screen.Dashboard) }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profile by viewModel.userProfile.collectAsState()

    val transcribeService = remember { AudioTranscribeService(context) }
    DisposableEffect(Unit) {
        onDispose { transcribeService.destroy() }
    }

    val isRecording by transcribeService.isRecording.collectAsState()
    val isTranscribing by transcribeService.isTranscribing.collectAsState()
    val liveTranscript by transcribeService.livePartialTranscript.collectAsState()
    val finalTranscript by transcribeService.finalTranscript.collectAsState()
    val errorMessage by transcribeService.errorMessage.collectAsState()

    var editableTranscript by remember { mutableStateOf("") }
    var saveStatusMessage by remember { mutableStateOf<String?>(null) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(liveTranscript) {
        if (liveTranscript.isNotBlank()) {
            editableTranscript = liveTranscript
        }
    }
    LaunchedEffect(finalTranscript) {
        if (finalTranscript.isNotBlank()) {
            editableTranscript = finalTranscript
        }
    }

    // Recording duration timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (true) {
                delay(1000L)
                recordingSeconds++
            }
        }
    }

    // Microphone permission launcher
    var hasRecordAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasRecordAudioPermission = granted
        if (granted) {
            transcribeService.startListening()
        }
    }

    // Mic pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Clinical Voice Dictation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            "Model: gemini-3.5-transcribe",
                            fontSize = 11.sp,
                            color = MedTealAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedNavyPrimary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Model Capability Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MedTealPrimary.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MedTealPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "gemini-3.5-transcribe",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = MedNavyPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MedTealPrimary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "SPEECH-TO-TEXT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Microphone speech transcription optimized for medical terminology, case notes, and viva practice.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Microphone Recording Cockpit
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isRecording) "Listening to microphone..." else "Tap microphone to record",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isRecording) Color(0xFFD32F2F) else MedNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isRecording) {
                                val mins = recordingSeconds / 60
                                val secs = recordingSeconds % 60
                                String.format("Recording: %02d:%02d", mins, secs)
                            } else {
                                "Supports medical abbreviations, symptoms, and dosages"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Mic Button with Pulse
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(90.dp)
                                .scale(if (isRecording) pulseScale else 1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isRecording) Color(0xFFD32F2F) else MedTealPrimary,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (isRecording) {
                                            transcribeService.stopListening()
                                            scope.launch {
                                                if (editableTranscript.isNotBlank()) {
                                                    transcribeService.transcribeAudioInput(
                                                        editableTranscript,
                                                        profile
                                                    )
                                                }
                                            }
                                        } else {
                                            saveStatusMessage = null
                                            if (hasRecordAudioPermission) {
                                                transcribeService.startListening()
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    }
                                    .testTag("mic_record_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = if (isRecording) "Stop Recording" else "Start Microphone Recording",
                                        tint = Color.White,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isRecording) {
                                OutlinedButton(
                                    onClick = {
                                        transcribeService.stopListening()
                                    }
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Stop")
                                }
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        if (editableTranscript.isNotBlank()) {
                                            transcribeService.transcribeAudioInput(editableTranscript, profile)
                                        }
                                    }
                                },
                                enabled = !isRecording && editableTranscript.isNotBlank() && !isTranscribing,
                                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                                modifier = Modifier.testTag("transcribe_with_model_button")
                            ) {
                                if (isTranscribing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Transcribing...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Format with gemini-3.5-transcribe")
                                }
                            }
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Quick Preset Audio Scenarios
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Clinical Dictation Quick Presets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MedNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                editableTranscript = "Patient presents with acute epigastric pain radiating to the back, elevated serum amylase and lipase, guarding on palpation."
                                scope.launch {
                                    transcribeService.transcribeAudioInput(editableTranscript, profile)
                                }
                            },
                            label = { Text("Acute Pancreatitis", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, null, modifier = Modifier.size(14.dp)) }
                        )
                        AssistChip(
                            onClick = {
                                editableTranscript = "Renin is secreted by juxtaglomerular cells in response to low renal perfusion, converting angiotensinogen to angiotensin 1."
                                scope.launch {
                                    transcribeService.transcribeAudioInput(editableTranscript, profile)
                                }
                            },
                            label = { Text("RAAS Mechanism", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.School, null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            // Transcribed Text Area
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Transcription Output",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MedNavyPrimary
                            )
                            if (editableTranscript.isNotBlank()) {
                                TextButton(
                                    onClick = { editableTranscript = "" }
                                ) {
                                    Text("Clear", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editableTranscript,
                            onValueChange = { editableTranscript = it },
                            placeholder = {
                                Text("Spoken words transcribed from microphone will appear here...")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 140.dp)
                                .testTag("transcript_output_field"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Save to Notes, Send to AI Tutor
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (editableTranscript.isNotBlank()) {
                                        viewModel.savePersonalNote(
                                            subjectName = "Clinical Ward Posting",
                                            unitName = "Voice Dictation",
                                            title = "Voice Note (${profile?.courseName ?: "General"})",
                                            content = editableTranscript,
                                            category = "Ward Dictation"
                                        )
                                        saveStatusMessage = "Saved to Study Notes!"
                                    }
                                },
                                enabled = editableTranscript.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_transcript_note_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Note", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (editableTranscript.isNotBlank()) {
                                        viewModel.sendAiPrompt("Analyze this transcribed clinical voice note: $editableTranscript")
                                        viewModel.navigateTo(Screen.AiTutor)
                                    }
                                },
                                enabled = editableTranscript.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ask_ai_transcript_button")
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask AI Tutor", fontSize = 12.sp)
                            }
                        }

                        if (saveStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = saveStatusMessage ?: "",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
