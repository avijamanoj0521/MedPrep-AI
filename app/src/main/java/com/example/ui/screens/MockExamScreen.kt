package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlinx.coroutines.delay

@Composable
fun MockExamScreen(viewModel: MedPrepViewModel) {
    val enrolledSubjects by viewModel.enrolledSubjects.collectAsState()
    val allQuestions by viewModel.questionsForCourse.collectAsState()

    var isExamLive by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(1800) } // 30 minutes
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    val examQuestions = remember(allQuestions) {
        if (allQuestions.isNotEmpty()) allQuestions else SeedData.initialQuestions()
    }

    val answers = remember { mutableStateMapOf<Int, Int>() }
    val markedForReview = remember { mutableStateListOf<Int>() }
    var isExamFinished by remember { mutableStateOf(false) }

    // Countdown Timer
    LaunchedEffect(isExamLive, remainingSeconds) {
        if (isExamLive && remainingSeconds > 0 && !isExamFinished) {
            delay(1000L)
            remainingSeconds--
        } else if (isExamLive && remainingSeconds == 0 && !isExamFinished) {
            isExamFinished = true
        }
    }

    if (!isExamLive && !isExamFinished) {
        // Pre-Exam Setup
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("mock_exam_setup_screen")
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = MedNavyPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "University Examination Simulator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Realistic internal assessment and terminal examination conditions with timed question palette",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedTealAccent,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Examination Instructions & Protocol", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Total Duration: 30 Minutes\n• Section: High-Yield University Assessment\n• Question Palette Legend:\n   🟢 Answered   🟠 Marked for Review   ⚪ Unattempted\n• Auto-submission activates when the timer expires.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isExamLive = true
                                remainingSeconds = 1800
                                currentQuestionIdx = 0
                                answers.clear()
                                markedForReview.clear()
                                isExamFinished = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("start_mock_exam_button")
                        ) {
                            Text("Begin University Mock Exam")
                        }
                    }
                }
            }
        }
    } else if (isExamFinished) {
        // Exam Result Analysis
        val total = examQuestions.size
        var correct = 0
        examQuestions.forEachIndexed { idx, q ->
            if (answers[idx] == q.correctOptionIndex) correct++
        }
        val scorePercent = if (total > 0) (correct * 100) / total else 0

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Examination Concluded",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MedNavyPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Terminal Assessment Result: $correct / $total ($scorePercent%)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (scorePercent >= 50) Color(0xFF16A34A) else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (scorePercent >= 50) "PASSED with university standard criteria." else "SUPPLEMENTARY revision advised for weak areas.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.recordQuizAttempt(
                        testTitle = "University Mock Terminal Exam",
                        subjectName = examQuestions.firstOrNull()?.subjectName ?: "Core Subjects",
                        score = correct,
                        totalQuestions = total,
                        timeTakenSeconds = 1800 - remainingSeconds
                    )
                    isExamLive = false
                    isExamFinished = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Exam Audit & Return")
            }
        }
    } else {
        // Live Exam Interface
        val currentQ = examQuestions[currentQuestionIdx]
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Live Header with Timer & Submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (remainingSeconds < 300) Color(0xFFFFEBEE) else MedNavyPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "⏱️ %02d:%02d".format(minutes, seconds),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (remainingSeconds < 300) MaterialTheme.colorScheme.error else MedNavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = { isExamFinished = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("submit_exam_early_button")
                ) {
                    Text("Submit Paper", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Palette Navigation
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(examQuestions.indices.toList()) { idx ->
                    val isCurrent = idx == currentQuestionIdx
                    val isAnswered = answers.containsKey(idx)
                    val isReview = markedForReview.contains(idx)

                    val bgColor = when {
                        isCurrent -> MedNavyPrimary
                        isReview -> Color(0xFFF59E0B)
                        isAnswered -> Color(0xFF16A34A)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val textColor = if (isCurrent || isReview || isAnswered) Color.White else MaterialTheme.colorScheme.onSurface

                    Surface(
                        shape = CircleShape,
                        color = bgColor,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { currentQuestionIdx = idx }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question Body
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Question ${currentQuestionIdx + 1} • ${currentQ.subjectName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MedTealPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQ.questionText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Options
            listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD).forEachIndexed { optIdx, optText ->
                val isSelected = answers[currentQuestionIdx] == optIdx
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
                        .padding(vertical = 4.dp)
                        .clickable { answers[currentQuestionIdx] = optIdx }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { answers[currentQuestionIdx] = optIdx }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = optText, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Palette Control Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        if (markedForReview.contains(currentQuestionIdx)) {
                            markedForReview.remove(currentQuestionIdx)
                        } else {
                            markedForReview.add(currentQuestionIdx)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (markedForReview.contains(currentQuestionIdx)) Icons.Default.BookmarkRemove else Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (markedForReview.contains(currentQuestionIdx)) "Unmark" else "Mark Review", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (currentQuestionIdx < examQuestions.size - 1) {
                            currentQuestionIdx++
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
                ) {
                    Text("Next Question", fontSize = 12.sp)
                }
            }
        }
    }
}
