package com.aiearcoach.app.audio

import kotlin.math.sqrt

/**
 * Simple Energy-based Voice Activity Detection
 * In a real-world app, we'd use Silero or WebRTC VAD.
 */
class SimpleVAD(private val threshold: Double = 500.0) {
    
    fun isSpeech(audioData: ShortArray): Boolean {
        var sum = 0.0
        for (sample in audioData) {
            sum += sample * sample
        }
        val rms = sqrt(sum / audioData.size)
        return rms > threshold
    }
}
