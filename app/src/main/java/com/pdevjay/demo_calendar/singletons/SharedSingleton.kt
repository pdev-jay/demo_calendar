package com.pdevjay.demo_calendar.singletons

import com.pdevjay.demo_calendar.data_models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSingleton @Inject constructor() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    fun updateDate(newDate: LocalDate) {
        _selectedDate.value = newDate
    }


    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun updateTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
    }
}