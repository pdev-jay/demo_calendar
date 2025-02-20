package com.pdevjay.demo_calendar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdevjay.demo_calendar.data_models.CalendarDay
import com.pdevjay.demo_calendar.data_models.CalendarState
import com.pdevjay.demo_calendar.data_models.Task
import com.pdevjay.demo_calendar.intents.CalendarIntent
import com.pdevjay.demo_calendar.singletons.SharedSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val sharedSingleton: SharedSingleton
) : ViewModel() {
    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState: StateFlow<CalendarState> = _calendarState.asStateFlow()

    init {
        loadMonth(LocalDate.now())
        viewModelScope.launch {
            sharedSingleton.selectedDate.collect { date ->
//                updateCalendarDays(date)
                updateCalendarState(date)
            }
        }

        viewModelScope.launch {
            sharedSingleton.tasks.collect {
                updateCalendarTaskCounts()
            }
        }
    }

    fun processIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.LoadMonth -> loadMonth(intent.month)
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

        val days = mutableListOf<CalendarDay>()

        // Previous month days
        for (i in (firstDayOfMonth - 1) downTo 0) {
            val day = previousMonth.lengthOfMonth() - i
            days.add(CalendarDay(previousMonth.withDayOfMonth(day)))
        }

        // Current month days
        for (day in 1..daysInMonth) {
            days.add(CalendarDay(month.withDayOfMonth(day)))
        }

        // Next month days
        val remainingDays = 42 - days.size // To fill 6 weeks (7x6 grid)
        for (day in 1..remainingDays) {
            days.add(CalendarDay(nextMonth.withDayOfMonth(day)))
        }

        _calendarState.value = _calendarState.value.copy(currentMonth = month, days = days)

        // 업데이트된 달력 상태를 사용하여 Task 개수 업데이트
        updateCalendarTaskCounts()
    }

    private fun updateCalendarState(date: LocalDate){
        _calendarState.update {
            it.copy(selectedDate = date)
        }
    }

    private fun selectDate(date: LocalDate){
        sharedSingleton.updateDate(date)
    }

    private fun updateCalendarTaskCounts() {
        val tasks = sharedSingleton.tasks.value
        val updatedDays = _calendarState.value.days.map { day ->
            val taskCount = tasks.count { it.date == day.date }
            val completedCount = tasks.count { it.date == day.date && it.isCompleted }
            day.copy(taskCount = taskCount, completedCount = completedCount)
        }
        _calendarState.update {
            it.copy(days = updatedDays)
        }
    }
}