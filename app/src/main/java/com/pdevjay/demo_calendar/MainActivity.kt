package com.pdevjay.demo_calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdevjay.demo_calendar.ui.theme.Demo_calendarTheme
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Demo_calendarTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    CustomCalendar()
                }
            }
        }
    }
}


@Composable
fun CustomCalendar(modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val taskDates = remember { setOf(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5)) }

    Column(modifier = modifier.padding(16.dp)) {
        // Month header
         CalendarHeader(currentMonth){ newMonth ->
             currentMonth = newMonth
         }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekday headers
        WeekSection()

        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        DaysGrid(currentMonth, selectedDate, taskDates){ newDate ->
            selectedDate = newDate
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected date display
        Text(text = "Selected Date: ${selectedDate}", fontSize = 18.sp)
    }
}

@Composable
private fun DaysGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    taskDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday

    val completedTaskCount = 3 // 완료된 태스크 개수
    val maxTaskCount = 10 // 최대 태스크 개수
    val lineWidth = getLineWidth(completedTaskCount, maxTaskCount) // 4dp에서 20dp 사이에서 완료율에 따라 길이 조절
    // 색상 계산 함수
    val indicatorColor = getIndicatorColor(lineWidth)

    Column {
        var dayCounter = 1
        for (week in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    val day =
                        if (week == 0 && i < firstDayOfMonth) null else if (dayCounter <= daysInMonth) dayCounter++ else null

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = day != null) {
                                onDateSelected(
                                    LocalDate.of(
                                        currentMonth.year,
                                        currentMonth.month,
                                        day!!
                                    )
                                )
                            }
                            .background(
                                color = getBackgroundColorForDay(day, selectedDate, currentMonth.month),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day?.toString() ?: "",
                                color = getTextColorForDay(day, currentMonth),
                                fontSize = 16.sp
                            )
                            // Task indicator line
                            Spacer(
                                modifier = Modifier
                                    .height(4.dp)
                                    .width(
                                        if (hasTaskForDay(day, currentMonth, taskDates)) lineWidth else 0.dp
                                    )
                                    .background(
                                        if (hasTaskForDay(day, currentMonth, taskDates)) indicatorColor else Color.Transparent
                                    )
                                    .align(Alignment.Start) // 좌측 정렬
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun getIndicatorColor(lineWidth: Dp): Color {
    val indicatorColor = when {
        lineWidth.value <= 6 -> Color(0xFFFF0000)      // 짧을 때 (진행도 낮음)
        lineWidth.value <= 19 -> Color(0xFFFF5E00)  // 중간 길이 (진행도 중간)
        else -> Color(0xFF1FDA11)                    // 길 때 (진행도 완료)
    }
    return indicatorColor
}

@Composable
private fun getLineWidth(
    completedTaskCount: Int,
    maxTaskCount: Int,
): Dp {
    val lineWidth = (completedTaskCount.toFloat() / maxTaskCount * 20).coerceIn(
        4f,
        20f
    ).dp // 4dp에서 20dp 사이에서 완료율에 따라 길이 조절
    return lineWidth
}

@Composable
private fun WeekSection() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in daysOfWeek) {
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarHeader(currentMonth: YearMonth, onMonthChange: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "<",
            fontSize = 24.sp,
            modifier = Modifier.clickable {
                onMonthChange(currentMonth.minusMonths(1))
            }
        )
        Text(
            text = "${
                currentMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                )
            } ${currentMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = ">",
            fontSize = 24.sp,
            modifier = Modifier.clickable {
                onMonthChange(currentMonth.plusMonths(1))
            }
        )
    }
}

// 백그라운드 색상

fun getBackgroundColorForDay(day: Int?, selectedDate: LocalDate, currentMonth: Month): Color {
    val isDaySelected = day != null && selectedDate.dayOfMonth == day
    val isCurrentMonth = selectedDate.month == currentMonth

    return if (isDaySelected && isCurrentMonth) {
        Color.LightGray
    } else {
        Color.Transparent
    }
}

// 날짜에 해당하는 태스크가 있는지 확인하는 함수
private fun hasTaskForDay(day: Int?, currentMonth: YearMonth, taskDates: Set<LocalDate>): Boolean {
    return day != null && taskDates.any { it.dayOfMonth == day && it.month == currentMonth.month }
}

// 오늘 날짜인지 확인하는 함수
private fun isToday(day: Int?, currentMonth: YearMonth): Boolean {
    val today = LocalDate.now()
    return day != null && day == today.dayOfMonth && currentMonth == YearMonth.from(today)
}

// 날짜에 따른 텍스트 색상을 결정하는 함수
private fun getTextColorForDay(day: Int?, currentMonth: YearMonth, defaultColor: Color = Color.Black): Color {
    return if (isToday(day, currentMonth)) Color.Red else defaultColor
}



@Composable
fun HabitItem(habit: String) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (checked) Color.Green.copy(alpha = 0.3f) else Color.Transparent)
            .clickable { checked = !checked }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(habit, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(){
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = convertMillisToLocalDate(datePickerState.selectedDateMillis ?: System.currentTimeMillis()),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Button(onClick = { openDialog = true }) {
                Text(text = "날짜 선택")
            }
        }

        if (openDialog) {
            DatePickerDialog(
                onDismissRequest = { openDialog = false },
                confirmButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text("취소")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }

}

fun convertMillisToLocalDate(millis: Long): String {
    val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()

    return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Demo_calendarTheme {
        CustomCalendar()
    }
}