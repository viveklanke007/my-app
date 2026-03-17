package com.aiearcoach.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiearcoach.app.data.CoachingDao
import com.aiearcoach.app.data.CoachingLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(private val coachingDao: CoachingDao) : ViewModel() {
    
    val logs: StateFlow<List<CoachingLog>> = coachingDao.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
