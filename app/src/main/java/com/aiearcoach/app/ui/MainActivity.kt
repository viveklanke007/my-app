package com.aiearcoach.app.ui

import android.Manifest
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.aiearcoach.app.service.CoachBackgroundService

import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiearcoach.app.data.AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO, 
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.POST_NOTIFICATIONS
            ),
            0
        )

        val db = AppDatabase.getInstance(this)
        val coachingDao = db.coachingDao()

        setContent {
            var currentScreen by remember { mutableStateOf("main") }
            
            MaterialTheme {
                when (currentScreen) {
                    "main" -> MainScreen(
                        onToggleCoach = { isEnabled ->
                            val intent = Intent(this, CoachBackgroundService::class.java)
                            if (isEnabled) {
                                startForegroundService(intent)
                            } else {
                                stopService(intent)
                            }
                        },
                        onNavigateToProgress = { currentScreen = "progress" }
                    )
                    "progress" -> {
                        val viewModel: ProgressViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return ProgressViewModel(coachingDao) as T
                            }
                        })
                        val logs by viewModel.logs.collectAsState()
                        Scaffold(
                            topBar = {
                                Button(onClick = { currentScreen = "main" }, modifier = Modifier.padding(8.dp)) {
                                    Text("Back")
                                }
                            }
                        ) { padding ->
                            Box(modifier = Modifier.padding(padding)) {
                                ProgressScreen(logs = logs)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onToggleCoach: (Boolean) -> Unit, onNavigateToProgress: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var coachEnabled by remember { mutableStateOf(false) }
    var focusModeEnabled by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "AI Ear Coach", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Status: ${if (coachEnabled) "Listening" else "Idle"}")
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Coach Mode")
                Switch(
                    checked = coachEnabled,
                    onCheckedChange = {
                        coachEnabled = it
                        onToggleCoach(it)
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Focus Mode")
                Switch(
                    checked = focusModeEnabled,
                    onCheckedChange = { 
                        focusModeEnabled = it 
                        val intent = Intent(context, CoachBackgroundService::class.java)
                        intent.putExtra("FOCUS_MODE", it)
                        context.startForegroundService(intent)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onNavigateToProgress) {
                Text("View Progress & Logs")
            }
        }
    }
}
