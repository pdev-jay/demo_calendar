package com.pdevjay.demo_calendar.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdevjay.demo_calendar.data_models.Task
import com.pdevjay.demo_calendar.intents.TaskIntent
import com.pdevjay.demo_calendar.ui.theme.Demo_calendarTheme
import com.pdevjay.demo_calendar.viewmodels.TaskViewModel

@Composable
fun TaskListView(innerPadding: PaddingValues, taskViewModel: TaskViewModel = viewModel()){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddButton(taskViewModel)
        TaskListContent(innerPadding, taskViewModel)
    }
}

@Composable
private fun AddButton(taskViewModel: TaskViewModel) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            taskViewModel.processIntent(
                TaskIntent.AddTask(
                    Task(
                        title = "New Task",
                        isCompleted = false
                    )
                )
            )
        }
    ) {
        Text(text = "Add Task")
    }
}

@Composable
fun TaskListContent(innerPadding: PaddingValues = PaddingValues(0.dp), taskViewModel: TaskViewModel){
    val taskState by taskViewModel.taskState.collectAsState()

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color.White),
    ){
        items(taskState.tasks.size) { index ->
            TaskItem(task = taskState.tasks[index],
                onTaskClick = {taskViewModel.processIntent(TaskIntent.CompleteTask(index))},
                onDeleteClick = {taskViewModel.processIntent(TaskIntent.DeleteTask(index))})
        }
    }
}

@Composable
fun TaskItem(task: Task, onTaskClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .clickable(){
                        onTaskClick()
                    },
                text = task.title,
                style = TextStyle(textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None)
            )
            Icon(
              Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable {
                    onDeleteClick()
                }
            )
        }
    }
}


@Preview
@Composable
fun TaskListViewPreview() {
    Demo_calendarTheme {
        TaskListView(innerPadding = PaddingValues(0.dp))
    }
}