package com.pdevjay.demo_calendar.viewmodels

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdevjay.demo_calendar.data_models.Task
import com.pdevjay.demo_calendar.data_models.TaskState
import com.pdevjay.demo_calendar.intents.TaskIntent
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
class TaskViewModel @Inject constructor(
    private val sharedSingleton: SharedSingleton
) : ViewModel() {
    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    init {
        viewModelScope.launch {
            sharedSingleton.selectedDate.collect { date ->
                loadTasks(date)
            }
        }

        viewModelScope.launch {
            sharedSingleton.tasks.collect {
                 loadTasks(sharedSingleton.selectedDate.value)
            }
        }
    }

    fun processIntent(intent: TaskIntent) {
        when (intent) {
            is TaskIntent.AddTask -> addTask(intent.task)
            is TaskIntent.DeleteTask -> deleteTask(intent.id)
            is TaskIntent.ToggleTaskCompletion -> toggleTaskCompletion(intent.id)
            is TaskIntent.LoadTasks -> loadTasks(intent.date)
        }
    }

    private fun loadTasks(date: LocalDate) {
        _taskState.update { it.copy(tasks = sharedSingleton.tasks.value.filter { it.date == date }) }
    }

    private fun addTask(task: Task) {
        val newTask = Task(title="new Task", isCompleted = false, date = sharedSingleton.selectedDate.value)
        sharedSingleton.updateTasks(sharedSingleton.tasks.value + newTask)
    }

    private fun deleteTask(id: String) {
        sharedSingleton.updateTasks(sharedSingleton.tasks.value.filterNot { it.id == id })
    }

    private fun toggleTaskCompletion(id: String) {
        sharedSingleton.updateTasks(sharedSingleton.tasks.value.map { task ->
            if (task.id == id) task.copy(isCompleted = !task.isCompleted) else task
        })
    }
}