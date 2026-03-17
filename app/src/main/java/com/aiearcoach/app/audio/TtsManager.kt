package com.aiearcoach.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "Language not supported")
            } else {
                isReady = true
                // Voice configuration for "Calm tone"
                tts?.setPitch(0.9f)
                tts?.setSpeechRate(0.9f)
            }
        } else {
            Log.e("TtsManager", "Initialization failed")
        }
    }

    fun speak(text: String) {
        if (isReady) {
            Log.d("TtsManager", "Speaking: $text")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CoachOutput")
        } else {
            Log.w("TtsManager", "TTS not ready yet")
        }
    }

    fun shutDown() {
        tts?.stop()
        tts?.shutdown()
    }
}
