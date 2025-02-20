package com.pdevjay.demo_calendar.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdevjay.demo_calendar.ui.theme.Demo_calendarTheme


@Composable
fun MainView(innerPadding: PaddingValues){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomCalendar(innerPadding = innerPadding)
        TaskListView(innerPadding = innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    Demo_calendarTheme {
        MainView(innerPadding = PaddingValues(0.dp))
    }
}