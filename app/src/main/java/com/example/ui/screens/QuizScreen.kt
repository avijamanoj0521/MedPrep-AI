package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    val questions by viewModel.questionsForCourse.collectAsState()
    val quizAttempts by viewModel.quizAttempts.collectAsState()

    var isQuizActive by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var userAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) } // questionIndex -> selectedOption
    var showExplanation by remember { mutableStateOf(false) }
    var isQuizSubmitted by remember { mutableStateOf(false) }

    // Quiz Questions pool
    val activeQuestions = remember(questions) {
        if (questions.isNotEmpty()) questions else com.example.data.local.SeedData.initialQuestions()
    }

    if (!isQuizActive) {
        // Quiz Hub: Choose Quiz or View Past Attempts
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("quiz_hub_screen")
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
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
                            text = "Medical & Healthcare MCQ Question Bank",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Course-specific practice with verified rationales and clinical explanations",
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MedTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Standard High-Yield Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Test your knowledge on current semester subjects (${activeQuestions.size} Questions available). Instant rationale shown after every answer or post-submission review.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                isQuizActive = true
                                currentQuestionIndex = 0
                                selectedOption = null
                                userAnswers = mutableMapOf()
                                showExplanation = false
                                isQuizSubmitted = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_quiz_button")
                        ) {
                            Text("Start Practice Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Past Quiz Attempts & Performance History:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MedNavyPrimary
                )
            }

            if (quizAttempts.isEmpty()) {
                item {
                    Text(
                        text = "No recorded quiz attempts yet. Complete a quiz to track your accuracy!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                            Column {
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

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (attempt.score >= attempt.totalQuestions / 2) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = "${attempt.score} / ${attempt.totalQuestions}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (attempt.score >= attempt.totalQuestions / 2) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else if (isQuizSubmitted) {
        // Quiz Score Summary Screen
        val total = activeQuestions.size
        var correctCount = 0
        activeQuestions.forEachIndexed { idx, q ->
            if (userAnswers[idx] == q.correctOptionIndex) correctCount++
        }
        val accuracy = if (total > 0) (correctCount * 100) / total else 0

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (accuracy >= 60) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (accuracy >= 60) Icons.Default.CheckCircle else Icons.Default.PriorityHigh,
                        contentDescription = null,
                        tint = if (accuracy >= 60) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quiz Completed!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MedNavyPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Score: $correctCount / $total ($accuracy% Accuracy)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MedTealPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (accuracy >= 75) "Outstanding clinical mastery! Keep up the great consistency."
                else "Good attempt! Review the rationales and difficult chapters in the syllabus.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.recordQuizAttempt(
                        testTitle = "High-Yield Clinical Practice",
                        subjectName = activeQuestions.firstOrNull()?.subjectName ?: "Medical Core",
                        score = correctCount,
                        totalQuestions = total,
                        timeTakenSeconds = 120
                    )
                    isQuizActive = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.fillMaxWidth().testTag("return_to_hub_button")
            ) {
                Text("Save Result & Return to Hub")
            }
        }
    } else {
        // Active Quiz Player
        val question = activeQuestions[currentQuestionIndex]
        val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Quiz Progress & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${activeQuestions.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MedNavyPrimary
                )

                TextButton(onClick = { isQuizActive = false }) {
                    Text("Exit Quiz", color = MaterialTheme.colorScheme.error)
                }
            }

            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1) / activeQuestions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MedTealPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = question.subjectName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedTealPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = question.questionText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options List
            options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = question.correctOptionIndex == index

                val (bgColor, borderColor) = if (showExplanation) {
                    if (isCorrect) Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    else if (isSelected) Color(0xFFFFEBEE) to Color(0xFFC62828)
                    else MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.outline
                } else {
                    if (isSelected) MedTealPrimary.copy(alpha = 0.1f) to MedTealPrimary
                    else MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.outline
                }

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (!showExplanation) {
                                selectedOption = index
                                userAnswers[currentQuestionIndex] = index
                            }
                        }
                        .testTag("quiz_option_$index")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = borderColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + index).toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = borderColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optionText,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Explanation Section
            if (showExplanation) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Clinical Rationale & Explanation:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF166534)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 12.sp,
                            color = Color(0xFF14532D),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!showExplanation) {
                    Button(
                        onClick = { showExplanation = true },
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verify_answer_button")
                    ) {
                        Text("Verify Answer & Rationale")
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < activeQuestions.size - 1) {
                                currentQuestionIndex++
                                selectedOption = userAnswers[currentQuestionIndex]
                                showExplanation = false
                            } else {
                                isQuizSubmitted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("next_question_button")
                    ) {
                        Text(if (currentQuestionIndex < activeQuestions.size - 1) "Next Question" else "Submit Quiz")
                    }
                }
            }
        }
    }
}
