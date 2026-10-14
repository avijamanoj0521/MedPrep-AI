package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Production-ready Gemini API Client for MedPrep AI.
 * Uses the requested stable Gemini model: gemini-3.1-flash-lite.
 * API keys are never hardcoded in source code, but injected safely via BuildConfig.GEMINI_API_KEY.
 */
object GeminiClient {

    private const val TAG = "GeminiClient"
    const val MODEL_ID = "gemini-3.1-flash-lite"
    private const val GEMINI_ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_ID:generateContent"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Sends the student's actual question with collegiate healthcare context to gemini-3.1-flash-lite.
     * Returns a [Result] containing the genuine response or a clear, actionable error.
     */
    suspend fun generateTutorAnswer(
        question: String,
        profile: UserProfileEntity?,
        selectedSubject: String? = null,
        conversationHistory: List<Pair<String, String>> = emptyList() // (role, content)
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isBlank() || apiKey == "DEFAULT_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException(
                    "API_KEY_MISSING: Gemini API key is not configured. Please add your GEMINI_API_KEY in the Secrets panel in AI Studio or in .env."
                )
            )
        }

        try {
            val course = profile?.courseName ?: "Healthcare Education"
            val semester = profile?.semesterOrPhase ?: "Current Semester"
            val university = profile?.university ?: "State Health Sciences University"
            val subject = selectedSubject ?: "General Clinical & Biomedical Sciences"

            val systemInstructionText = """
                You are MedPrep AI, an expert clinical and academic tutor for collegiate medical, nursing, pharmacy, and allied healthcare students.
                Student Context:
                - Program: $course
                - Stage: $semester
                - Board/University: $university
                - Subject: $subject

                Instructions:
                1. Provide a comprehensive, medically accurate, and easy-to-understand explanation tailored to this student's level.
                2. Structure answers with clean headings:
                   • Clinical Definition & High-Yield Overview
                   • Physiological / Pharmacological Mechanism (Step-by-Step)
                   • Clinical Correlates & Practical Applications
                   • High-Yield University Exam & Viva Pearls
                3. Address the student's specific question directly. Do NOT repeat canned answers.
                4. Conclude with a brief reminder that this is for educational study only.
            """.trimIndent()

            val contentsArray = JSONArray()

            // Include relevant conversation history (up to last 6 turns)
            val recentHistory = conversationHistory.takeLast(6)
            for ((role, text) in recentHistory) {
                val turnObj = JSONObject()
                turnObj.put("role", if (role == "user") "user" else "model")
                val parts = JSONArray()
                parts.put(JSONObject().put("text", text))
                turnObj.put("parts", parts)
                contentsArray.put(turnObj)
            }

            // Current prompt
            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", "Subject: $subject\nQuestion: $question"))
            currentTurn.put("parts", currentParts)
            contentsArray.put(currentTurn)

            val payload = JSONObject().apply {
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", systemInstructionText))
                    }
                    put("parts", parts)
                }
                put("systemInstruction", systemInstructionObj)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.4)
                    put("topP", 0.95)
                    put("maxOutputTokens", 2048)
                }
                put("generationConfig", genConfig)
            }

            val requestUrl = "$GEMINI_ENDPOINT?key=$apiKey"
            val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody).optJSONObject("error")
                    errJson?.optString("message") ?: "HTTP error ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }

                Log.e(TAG, "Gemini API Error: $errorMsg (code=${response.code})")

                val friendlyError = when (response.code) {
                    400 -> "Bad Request: The model rejected the query format ($errorMsg)."
                    403 -> "Authentication Error: Your Gemini API Key is invalid or unauthorized. Please verify the key in the Secrets panel."
                    404 -> "Model Not Found: The model '$MODEL_ID' is unavailable for this API key tier."
                    429 -> "Quota Exceeded: Gemini API rate limit reached. Please wait a few moments before submitting again."
                    else -> "API Error (${response.code}): $errorMsg"
                }
                return@withContext Result.failure(Exception(friendlyError))
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("Gemini returned an empty candidate list."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val answerText = parts?.optJSONObject(0)?.optString("text").orEmpty()

            if (answerText.isBlank()) {
                return@withContext Result.failure(Exception("Gemini returned an empty response text."))
            }

            Result.success(answerText.trim())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to Gemini API", e)
            Result.failure(Exception("Network failure connecting to Gemini: ${e.localizedMessage}"))
        }
    }
}
