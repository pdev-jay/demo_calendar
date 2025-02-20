package com.pdevjay.demo_calendar.intents

import com.pdevjay.demo_calendar.data_models.Task
import java.time.LocalDate

sealed class TaskIntent {
    data class LoadTasks(val date: LocalDate) : TaskIntent()
    data class AddTask(val task: Task) : TaskIntent()
    data class DeleteTask(val id: String) : TaskIntent()
    data class ToggleTaskCompletion(val id: String) : TaskIntent()
}

