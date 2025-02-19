package com.pdevjay.demo_calendar.viewmodels

import androidx.lifecycle.ViewModel
import com.pdevjay.demo_calendar.data_models.CalendarData
import com.pdevjay.demo_calendar.data_models.CalendarState
import com.pdevjay.demo_calendar.intents.CalendarIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState: StateFlow<CalendarState> = _calendarState

    init {
        processIntent(CalendarIntent.LoadMonth)
    }

    fun processIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.LoadMonth -> loadMonth(_calendarState.value.currentMonth)
            is CalendarIntent.SelectDate -> selectDate(intent.date)
            is CalendarIntent.NextMonth -> loadMonth(_calendarState.value.currentMonth.plusMonths(1))
            is CalendarIntent.PreviousMonth -> loadMonth(_calendarState.value.currentMonth.minusMonths(1))
        }
    }

    private fun loadMonth(month: LocalDate) {
        val daysInMonth = month.lengthOfMonth()
        val firstDayOfMonth = month.withDayOfMonth(1).dayOfWeek.value % 7
        val previousMonth = month.minusMonths(1)
        val nextMonth = month.plusMonths(1)

        val days = mutableListOf<CalendarData>()

        // Previous month days
        for (i in (firstDayOfMonth - 1) downTo 0) {
            val day = previousMonth.lengthOfMonth() - i
            days.add(CalendarData(previousMonth.withDayOfMonth(day), isCurrentMonth = false))
        }

        // Current month days
        for (day in 1..daysInMonth) {
            days.add(CalendarData(month.withDayOfMonth(day), isCurrentMonth = true, hasTask = day % 3 == 0))
        }

        // Next month days
        val remainingDays = 42 - days.size // To fill 6 weeks (7x6 grid)
        for (day in 1..remainingDays) {
            days.add(CalendarData(nextMonth.withDayOfMonth(day), isCurrentMonth = false))
        }

        _calendarState.value = _calendarState.value.copy(currentMonth = month, days = days)
    }

    private fun selectDate(date: LocalDate) {
        _calendarState.value = _calendarState.value.copy(selectedDate = date)
    }
}