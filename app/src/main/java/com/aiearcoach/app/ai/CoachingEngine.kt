package com.aiearcoach.app.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class CoachingEngine {
    
    private val apiKey = "AIzaSyBn3nlv0qIiMhDcCpuRwQo-FUcNzTAxzXY"
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )
    
    private val systemPrompt = """
        You are AI Ear Coach.
        Give short, actionable guidance (max 1 sentence).
        Calm tone, direct instruction, no long explanations.
        The user is wearing earbuds and you are coaching them in real-time.
        Examples: "Relax your shoulders.", "Speak slowly.", "Maintain eye contact."
    """.trimIndent()

    suspend fun processSpeech(transcription: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text(systemPrompt)
                    text("User said: $transcription")
                }
            )
            response.text?.trim()
        } catch (e: Exception) {
            Log.e("CoachingEngine", "Error getting Gemini response", e)
            null
        }
    }
}
