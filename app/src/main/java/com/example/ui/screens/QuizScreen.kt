package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.QuizQuestionEntity
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@Composable
fun QuizScreen(viewModel: MedPrepViewModel) {
    val quizAttempts by viewModel.quizAttempts.collectAsState()
    val enrolledSubjects by viewModel.enrolledSubjects.collectAsState()

    var isQuizActive by remember { mutableStateOf(false) }
    var isQuizSubmitted by remember { mutableStateOf(false) }

    // Quiz Configuration
    var selectedQuestionCount by remember { mutableIntStateOf(10) } // 10 (Default) or 15
    var selectedSubjectFilter by remember { mutableStateOf("All Subjects") }

    // Active Quiz Session State
    var activeQuizQuestions by remember { mutableStateOf<List<QuizQuestionEntity>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) } // questionIndex -> selectedOptionIndex (0..3)
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }

    if (!isQuizActive) {
        // ==================== QUIZ HUB ====================
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("quiz_hub_screen")
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    color = MedNavyPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Medical & Healthcare MCQ Practice Exam",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "10 to 15 non-repeating verified questions with four choices and detailed clinical rationales",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedTealAccent,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Quiz Configurator Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("quiz_config_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MedTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start a Practice Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quiz Length Selector: 10 (Default) or 15 Questions
                        Text(
                            text = "Quiz Length:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MedNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = selectedQuestionCount == 10,
                                onClick = { selectedQuestionCount = 10 },
                                label = { Text("10 Questions (Standard)", fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    if (selectedQuestionCount == 10) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("select_10_questions_chip")
                            )

                            FilterChip(
                                selected = selectedQuestionCount == 15,
                                onClick = { selectedQuestionCount = 15 },
                                label = { Text("15 Questions (High-Yield)", fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    if (selectedQuestionCount == 15) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("select_15_questions_chip")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Subject Selection
                        Text(
                            text = "Target Subject:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MedNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val subjectOptions = listOf("All Subjects") + enrolledSubjects.map { it.name }.distinct()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            subjectOptions.take(3).forEach { subj ->
                                FilterChip(
                                    selected = selectedSubjectFilter == subj,
                                    onClick = { selectedSubjectFilter = subj },
                                    label = { Text(subj.take(18), fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val generated = viewModel.generateQuizQuestions(
                                    targetCount = selectedQuestionCount,
                                    subjectFilter = selectedSubjectFilter
                                )
                                activeQuizQuestions = generated
                                currentQuestionIndex = 0
                                userAnswers = emptyMap()
                                isQuizActive = true
                                isQuizSubmitted = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("start_quiz_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch $selectedQuestionCount-Question Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Past Attempts & Score History
            item {
                Text(
                    text = "Your Quiz History & Performance:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MedNavyPrimary
                )
            }

            if (quizAttempts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = MedTealPrimary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No recorded attempts for your account yet.", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Complete your first quiz to calculate accuracy and track study retention.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(quizAttempts) { attempt ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                                    text = attempt.testTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = attempt.subjectName,
                                    fontSize = 11.sp,
                                    color = MedTealPrimary
                                )
                            }

                            val percentage = (attempt.score * 100) / attempt.totalQuestions.coerceAtLeast(1)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (percentage >= 50) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = "${attempt.score}/${attempt.totalQuestions} ($percentage%)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (percentage >= 50) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else if (isQuizSubmitted) {
        // ==================== RESULTS & REVIEW SCREEN ====================
        val total = activeQuizQuestions.size
        val score = activeQuizQuestions.indices.count { idx ->
            userAnswers[idx] == activeQuizQuestions[idx].correctOptionIndex
        }
        val percentage = (score * 100) / total.coerceAtLeast(1)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("quiz_results_screen")
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Final Score Header Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (percentage >= 70) MedNavyPrimary else Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (percentage >= 70) Icons.Default.EmojiEvents else Icons.Default.Assessment,
                            contentDescription = null,
                            tint = if (percentage >= 70) MedTealAccent else Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (percentage >= 80) "Outstanding Performance! 🌟" else if (percentage >= 50) "Quiz Completed! 👍" else "Study Revision Recommended 📚",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score: $score / $total ($percentage%)",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MedTealAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Correct: $score  •  Incorrect: ${total - score}  •  Questions: $total",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { isQuizActive = false },
                        modifier = Modifier.weight(1f).testTag("quiz_back_to_hub_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quiz Hub")
                    }

                    Button(
                        onClick = {
                            val retake = viewModel.generateQuizQuestions(
                                targetCount = selectedQuestionCount,
                                subjectFilter = selectedSubjectFilter
                            )
                            activeQuizQuestions = retake
                            currentQuestionIndex = 0
                            userAnswers = emptyMap()
                            isQuizSubmitted = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        modifier = Modifier.weight(1f).testTag("quiz_retry_button")
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retake Quiz")
                    }
                }
            }

            // Detailed Rationales Header
            item {
                Text(
                    text = "Question-by-Question Review & Rationales:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MedNavyPrimary
                )
            }

            // Question Rationales List
            itemsIndexed(activeQuizQuestions) { idx, q ->
                val userChoice = userAnswers[idx]
                val isCorrect = userChoice == q.correctOptionIndex
                val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFF4ADE80) else Color(0xFFF87171)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFDC2626),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Question ${idx + 1} of $total",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(q.questionText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your Answer: " + (userChoice?.let { "${('A' + it)}: ${options.getOrNull(it)}" } ?: "Not Answered"),
                            fontSize = 11.sp,
                            color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold
                        )

                        if (!isCorrect) {
                            Text(
                                text = "Correct Answer: ${('A' + q.correctOptionIndex)}: ${options.getOrNull(q.correctOptionIndex)}",
                                fontSize = 11.sp,
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Clinical Rationale: ${q.explanation}",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // ==================== ACTIVE QUIZ (1 Question at a time) ====================
        val total = activeQuizQuestions.size
        val currentQ = activeQuizQuestions.getOrNull(currentQuestionIndex)

        if (currentQ == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading quiz questions...")
            }
            return
        }

        val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)
        val selectedIndex = userAnswers[currentQuestionIndex]

        Scaffold(
            topBar = {
                Surface(color = MedNavyPrimary, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of $total",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("quiz_progress_indicator")
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MedTealAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = currentQ.difficulty,
                                    color = MedTealAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1f) / total },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = MedTealAccent,
                            trackColor = MedNavyDark
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button (disabled on question 1)
                        OutlinedButton(
                            onClick = {
                                if (currentQuestionIndex > 0) {
                                    currentQuestionIndex--
                                }
                            },
                            enabled = currentQuestionIndex > 0,
                            modifier = Modifier.testTag("quiz_previous_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous")
                        }

                        // Next or Submit Button
                        if (currentQuestionIndex < total - 1) {
                            Button(
                                onClick = {
                                    currentQuestionIndex++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                                modifier = Modifier.testTag("quiz_next_button")
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Button(
                                onClick = { showSubmitConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier.testTag("quiz_submit_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit Quiz", fontWeight = FontWeight.Bold)
                            }
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = currentQ.subjectName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTealPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQ.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select One Answer Choice:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // 4 Answer Options (A, B, C, D)
                itemsIndexed(options) { idx, optionText ->
                    val isSelected = selectedIndex == idx
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MedTealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) MedTealPrimary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Save student choice in map, preserving it across Next and Previous navigation
                                userAnswers = userAnswers + (currentQuestionIndex to idx)
                            }
                            .testTag("quiz_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MedTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + idx).toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optionText,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Confirmation Dialog before Final Quiz Submission
        if (showSubmitConfirmDialog) {
            val answeredCount = userAnswers.size
            val unAnsweredCount = total - answeredCount

            AlertDialog(
                onDismissRequest = { showSubmitConfirmDialog = false },
                title = { Text("Submit Practice Quiz?", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "You have answered $answeredCount of $total questions." +
                                if (unAnsweredCount > 0) " There are $unAnsweredCount unanswered questions. Would you like to submit now?" else " Ready to calculate your final score?"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubmitConfirmDialog = false
                            val finalScore = activeQuizQuestions.indices.count { idx ->
                                userAnswers[idx] == activeQuizQuestions[idx].correctOptionIndex
                            }
                            // Save attempt to authenticated student's record
                            viewModel.recordQuizAttempt(
                                testTitle = "$total-Question Exam (${selectedSubjectFilter.take(15)})",
                                subjectName = selectedSubjectFilter,
                                score = finalScore,
                                totalQuestions = total,
                                timeTakenSeconds = 120
                            )
                            isQuizSubmitted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.testTag("confirm_submit_quiz_button")
                    ) {
                        Text("Confirm & Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitConfirmDialog = false }) {
                        Text("Return to Questions")
                    }
                }
            )
        }
    }
}
