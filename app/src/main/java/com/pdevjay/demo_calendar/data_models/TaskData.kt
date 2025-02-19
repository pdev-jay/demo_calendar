package com.pdevjay.demo_calendar.data_models

import java.time.LocalDate


data class Task(
    val id: String? = "",
    val title: String,
    val isCompleted: Boolean = false,
    val date: LocalDate? = null
)


// --- State ---
data class TaskState(
    val tasks: List<Task> = emptyList(),
)