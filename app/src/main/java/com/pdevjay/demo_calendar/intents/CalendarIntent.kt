package com.pdevjay.demo_calendar.intents

import com.pdevjay.demo_calendar.data_models.Task
import java.time.LocalDate

sealed class CalendarIntent {
    data class LoadMonth(val month: LocalDate) : CalendarIntent()
    data class SelectDate(val date: LocalDate) : CalendarIntent()
    object PreviousMonth: CalendarIntent()
    object NextMonth: CalendarIntent()
}

