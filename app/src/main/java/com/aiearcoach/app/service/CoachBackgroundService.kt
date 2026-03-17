package com.aiearcoach.app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aiearcoach.app.ai.CoachingEngine
import com.aiearcoach.app.audio.*
import com.aiearcoach.app.logic.SituationDetector
import com.aiearcoach.app.data.AppDatabase
import com.aiearcoach.app.data.CoachingLog
import kotlinx.coroutines.*
import android.content.pm.ServiceInfo
import android.os.Build

class CoachBackgroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private lateinit var audioManager: AudioCaptureManager
    private lateinit var vad: SimpleVAD
    private lateinit var ttsManager: TtsManager
    private lateinit var coachingEngine: CoachingEngine
    private lateinit var sttManager: SttManager
    private lateinit var situationDetector: SituationDetector
    private lateinit var db: AppDatabase
    private lateinit var bluetoothRouter: BluetoothAudioRouter

    companion object {
        private const val CHANNEL_ID = "CoachServiceChannel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "CoachBackgroundService"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        db = AppDatabase.getInstance(this)
        vad = SimpleVAD(threshold = 600.0)
        ttsManager = TtsManager(this)
        coachingEngine = CoachingEngine()
        situationDetector = SituationDetector()
        bluetoothRouter = BluetoothAudioRouter(this)
        
        sttManager = SttManager(this) { transcription ->
            handleTranscription(transcription)
        }

        audioManager = AudioCaptureManager { audioData ->
            if (vad.isSpeech(audioData)) {
                // Speech detected
            }
        }
    }

    private fun handleTranscription(text: String) {
        val lowerText = text.lowercase()
        
        if (lowerText.contains("coach") || lowerText.contains("help") || lowerText.contains("motivate")) {
            processWithAI(text)
            return
        }

        val situation = situationDetector.onSpeechDetected(text)
        if (situation == "stress") {
            ttsManager.speak("Deep breath. Speak slowly.")
            saveLog(text, "Deep breath. Speak slowly.", "stress")
        } else if (text.split(" ").size > 5) {
            if (!focusModeEnabled) {
                processWithAI(text)
            }
        }
    }

    private fun processWithAI(text: String) {
        serviceScope.launch {
            val response = coachingEngine.processSpeech(text)
            response?.let {
                ttsManager.speak(it)
                saveLog(text, it, "ai_coach")
            }
        }
    }

    private fun saveLog(text: String, response: String, situation: String) {
        serviceScope.launch(Dispatchers.IO) {
            db.coachingDao().insertLog(
                CoachingLog(
                    timestamp = System.currentTimeMillis(),
                    transcription = text,
                    response = response,
                    situation = situation
                )
            )
        }
    }

    private var focusModeEnabled = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val focusToggle = intent?.getBooleanExtra("FOCUS_MODE", false) ?: false
        focusModeEnabled = focusToggle
        
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        bluetoothRouter.startBluetoothSco()
        sttManager.startListening()
        audioManager.start()
        
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI Ear Coach Active")
            .setContentText("Listening and coaching...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "AI Ear Coach Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sttManager.stop()
        audioManager.stop()
        ttsManager.shutDown()
        bluetoothRouter.stopBluetoothSco()
        bluetoothRouter.dispose()
        serviceScope.cancel()
    }
}
