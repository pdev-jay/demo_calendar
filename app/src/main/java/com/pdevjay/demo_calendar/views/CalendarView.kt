package com.pdevjay.demo_calendar.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdevjay.demo_calendar.R
import com.pdevjay.demo_calendar.data_models.CalendarData
import com.pdevjay.demo_calendar.intents.CalendarIntent
import com.pdevjay.demo_calendar.ui.theme.Demo_calendarTheme
import com.pdevjay.demo_calendar.viewmodels.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun CustomCalendar(modifier: Modifier = Modifier, innerPadding: PaddingValues = PaddingValues(0.dp), calendarViewModel: CalendarViewModel = viewModel()) {
    val calendarState by calendarViewModel.calendarState.collectAsState()

    var slideDirection by remember { mutableIntStateOf(1) } // 1: 다음 달, -1: 이전 달

    Column(modifier = modifier.padding(innerPadding)) {
        // Month header
        CalendarHeader(calendarViewModel = calendarViewModel){ newMonth ->
            slideDirection = if (newMonth.monthValue > calendarState.currentMonth.monthValue) 1 else - 1
        }

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        // Weekday headers
        WeekSection()

        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        DaysGrid(slideDirection, calendarViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Selected date display
        Text(text = "Selected Date: ${calendarState.selectedDate ?: LocalDate.now().toString()}", fontSize = 18.sp)
    }
}

@Composable
private fun CalendarHeader(calendarViewModel: CalendarViewModel, onMonthChange: (LocalDate) -> Unit = {}) {
    val calendarState by calendarViewModel.calendarState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "<",
            fontSize = 24.sp,
            modifier = Modifier.clickable {
                onMonthChange(calendarState.currentMonth.minusMonths(1))
                calendarViewModel.processIntent(CalendarIntent.PreviousMonth)
            }
        )
        Text(
            text = "${
                calendarState.currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            } ${calendarState.currentMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = ">",
            fontSize = 24.sp,
            modifier = Modifier.clickable {
                onMonthChange(calendarState.currentMonth.plusMonths(1))
                calendarViewModel.processIntent(CalendarIntent.NextMonth)
            }
        )
    }
}

@Composable
private fun DaysGrid(
    slideDirection: Int,
    calendarViewModel: CalendarViewModel,
) {
    val calendarState by calendarViewModel.calendarState.collectAsState()

    val completedTaskCount = 10 // 완료된 태스크 개수
    val maxTaskCount = 10 // 최대 태스크 개수
    val lineWidth = getLineWidth(completedTaskCount, maxTaskCount) // 4dp에서 20dp 사이에서 완료율에 따라 길이 조절
    // 색상 계산 함수
    val indicatorColor = getIndicatorColor(lineWidth)

    AnimatedContent(
        targetState = calendarState.currentMonth,
        transitionSpec = {
            if (slideDirection > 0) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }

        }
    ) { targetMonth ->
        Column {
            calendarState.days.chunked(7).forEach{ week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach{ day ->
                        DateCell(
                            modifier = Modifier.weight(1f),
                            day = day,
                            isSelected = day.date == calendarState.selectedDate,
                            lineWidth,
                            indicatorColor,
                            onDateSelected = {calendarViewModel.processIntent(CalendarIntent.SelectDate(it))},
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    modifier: Modifier = Modifier,
    day: CalendarData,
    isSelected: Boolean,
    lineWidth: Dp,
    indicatorColor: Color,
    onDateSelected: (LocalDate) -> Unit
){
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onDateSelected(day.date) }
            .background(
                if (isSelected) Color.LightGray.copy(alpha = 0.3f) else Color.White,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when (day.date) {
                    LocalDate.now() -> Color.Red
                    in LocalDate.now().withDayOfMonth(1)..LocalDate.now()
                        .withDayOfMonth(LocalDate.now().lengthOfMonth()) -> Color.Black
                    else -> Color.Gray
                }
            )
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .width(lineWidth)
                    .background(if (day.hasTask) indicatorColor else Color.Transparent)
                    .align(Alignment.Start)
            )
        }
    }
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
private fun getIndicatorColor(lineWidth: Dp): Color {
    val indicatorColor = when {
        lineWidth.value <= 6 -> colorResource(R.color.progress_bar_color_red)      // 짧을 때 (진행도 낮음)
        lineWidth.value <= 19 -> colorResource(R.color.progress_bar_color_orange)  // 중간 길이 (진행도 중간)
        else -> colorResource(R.color.progress_bar_color_green)                    // 길 때 (진행도 완료)
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

@Preview(showBackground = true)
@Composable
fun CustomCalendarPreview() {
    Demo_calendarTheme {
        CustomCalendar(innerPadding = PaddingValues(0.dp))
    }
}