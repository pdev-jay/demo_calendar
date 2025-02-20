package com.pdevjay.demo_calendar.data_models

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.util.UUID


@Immutable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val date: LocalDate
)


// --- State ---
data class TaskState(
    val tasks: List<Task> = emptyList()
)
