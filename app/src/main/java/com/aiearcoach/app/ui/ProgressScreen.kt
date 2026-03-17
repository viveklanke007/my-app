package com.aiearcoach.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiearcoach.app.data.CoachingLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(logs: List<CoachingLog>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Activity Logs", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (logs.isEmpty()) {
            Text("No coaching sessions recorded yet.")
        } else {
            LazyColumn {
                items(logs) { log ->
                    LogItem(log)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun LogItem(log: CoachingLog) {
    val date = Date(log.timestamp)
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = format.format(date), style = MaterialTheme.typography.labelSmall)
            Text(text = log.situation.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Text(text = "You: ${log.transcription}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Coach: ${log.response}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
    }
}
