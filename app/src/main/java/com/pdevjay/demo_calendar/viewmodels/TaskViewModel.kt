package com.pdevjay.demo_calendar.viewmodels

import androidx.lifecycle.ViewModel
import com.pdevjay.demo_calendar.data_models.CalendarState
import com.pdevjay.demo_calendar.data_models.Task
import com.pdevjay.demo_calendar.data_models.TaskState
import com.pdevjay.demo_calendar.intents.TaskIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(): ViewModel(){
    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState

    init {
        processIntent(TaskIntent.LoadTasks)
    }

    fun processIntent(intent: TaskIntent){
        when(intent){
            is TaskIntent.LoadTasks -> loadTasks()
            is TaskIntent.CompleteTask -> completeTask(intent.index)
            is TaskIntent.DeleteTask -> deleteTask(intent.index)
            is TaskIntent.AddTask -> addTask(intent.task)
            else -> {}
        }
    }

    private fun loadTasks(){
        _taskState.value = _taskState.value.copy(tasks = listOf(Task(title = "Task 1", isCompleted = true), Task(title = "Task 2", isCompleted = false)))
    }

    private fun completeTask(index: Int){
        _taskState.value = _taskState.value.copy(
            tasks = _taskState.value.tasks.mapIndexed { i, task ->
                if (i == index) task.copy(isCompleted = !task.isCompleted) else task
            }
        )
    }

    private fun addTask(task: Task){
        _taskState.value = _taskState.value.copy(tasks = _taskState.value.tasks + task)
    }

    private fun deleteTask(index: Int){
        _taskState.value = _taskState.value.copy(tasks = _taskState.value.tasks.filterIndexed { i, _ -> i != index })
    }
}