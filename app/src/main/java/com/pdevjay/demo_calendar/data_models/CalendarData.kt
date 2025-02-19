package com.pdevjay.demo_calendar.data_models

import androidx.compose.runtime.Immutable
import java.time.LocalDate

// --- Data Class ---
@Immutable
data class CalendarData(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val hasTask: Boolean = false,
)

// --- State ---
data class CalendarState(
    val currentMonth: LocalDate = LocalDate.now(),
    val days: List<CalendarData> = emptyList(),
    val selectedDate: LocalDate? = null,
)

data class Task(val title: String, val date: LocalDate)
