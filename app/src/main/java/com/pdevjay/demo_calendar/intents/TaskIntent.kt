package com.pdevjay.demo_calendar.intents

import com.pdevjay.demo_calendar.data_models.Task

sealed class TaskIntent {
    object LoadTasks : TaskIntent()
    data class CompleteTask(val index: Int) : TaskIntent()
    data class DeleteTask(val index: Int) : TaskIntent()
    data class AddTask(val task: Task) : TaskIntent()
}