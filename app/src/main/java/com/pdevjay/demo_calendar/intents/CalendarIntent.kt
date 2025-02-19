package com.pdevjay.demo_calendar.intents

import java.time.LocalDate

// --- Intent ---
sealed class CalendarIntent {
    object LoadMonth : CalendarIntent()
    data class SelectDate(val date: LocalDate) : CalendarIntent()
    object NextMonth : CalendarIntent()
    object PreviousMonth : CalendarIntent()
}
