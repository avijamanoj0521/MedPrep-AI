package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardEntity
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(viewModel: MedPrepViewModel) {
    val flashcards by viewModel.flashcards.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val activeFlashcard = flashcards.getOrNull(currentIndex)

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flashcard_flip"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MedTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_flashcard_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Flashcard")
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
                        text = "High-Yield Spaced Repetition Flashcards",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Anatomy, Pharmacology, Normal Lab Values & Pathophysiology",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MedTealAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (flashcards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No flashcards found. Tap '+' to create your first card!")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Card ${currentIndex + 1} of ${flashcards.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Flip Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlipped) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { isFlipped = !isFlipped }
                            .testTag("flashcard_flip_target")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.graphicsLayer {
                                    if (rotation > 90f) rotationY = 180f
                                }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MedTealPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = activeFlashcard?.category ?: "Core Concept",
                                        color = MedTealPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (!isFlipped) (activeFlashcard?.front ?: "")
                                    else (activeFlashcard?.back ?: ""),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (isFlipped) MedNavyPrimary else MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (!isFlipped) "Tap card to reveal answer" else "Tap card to flip back",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Spaced Repetition Mastery Rating (Again, Hard, Good, Easy)
                    Text(
                        text = "Rate your recall ease (Spaced Repetition):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                activeFlashcard?.let { viewModel.updateFlashcardMastery(it, 1) }
                                isFlipped = false
                                if (currentIndex < flashcards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Again", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                activeFlashcard?.let { viewModel.updateFlashcardMastery(it, 2) }
                                isFlipped = false
                                if (currentIndex < flashcards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hard", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                activeFlashcard?.let { viewModel.updateFlashcardMastery(it, 3) }
                                isFlipped = false
                                if (currentIndex < flashcards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Good", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                activeFlashcard?.let { viewModel.updateFlashcardMastery(it, 4) }
                                isFlipped = false
                                if (currentIndex < flashcards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Easy", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateFlashcardDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { subject, cat, front, back ->
                viewModel.saveFlashcard(subject, cat, front, back)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateFlashcardDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("Pharmacology") }
    var category by remember { mutableStateOf("Drug Mechanism") }
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Flashcard", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = front, onValueChange = { front = it }, label = { Text("Front (Question / Prompt)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = back, onValueChange = { back = it }, label = { Text("Back (Answer / Rationale)") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Anatomy, Normal Lab Values, etc.)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank()) {
                        onSave(subject, category, front, back)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
            ) {
                Text("Create Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
