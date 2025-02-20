package com.pdevjay.demo_calendar.data_models

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class CalendarDay(
    val date: LocalDate,
    val taskCount: Int = 0,
    val completedCount: Int = 0,
) {
    val progress: Float
        get() = if (taskCount > 0) completedCount.toFloat() / taskCount else 0f
}

data class CalendarState(
    val currentMonth: LocalDate = LocalDate.now(),
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now()
)
