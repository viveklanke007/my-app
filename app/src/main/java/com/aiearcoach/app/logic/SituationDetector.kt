package com.aiearcoach.app.logic

import android.util.Log

class SituationDetector {

    private var speechStartTime: Long = 0
    private var lastSpeechTime: Long = System.currentTimeMillis()
    private var wordsCount: Int = 0

    fun onSpeechDetected(transcription: String): String {
        val now = System.currentTimeMillis()
        val words = transcription.trim().split("\\s+".toRegex())
        wordsCount += words.size
        
        lastSpeechTime = now
        
        // Simple fast speech detection (WPM calculation)
        // If they speak more than 3 words in a very short burst
        if (words.size > 3) {
            // Placeholder logic for "Stress Mode"
            return "stress"
        }

        return "conversation"
    }

    fun checkIdle(): Boolean {
        val now = System.currentTimeMillis()
        // If silence > 20 minutes (1200000 ms)
        return (now - lastSpeechTime) > 1200000
    }
}
