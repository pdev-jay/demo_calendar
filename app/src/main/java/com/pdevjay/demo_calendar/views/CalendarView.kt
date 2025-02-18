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
import com.pdevjay.demo_calendar.R
import com.pdevjay.demo_calendar.ui.theme.Demo_calendarTheme
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomCalendar(modifier: Modifier = Modifier, innerPadding: PaddingValues = PaddingValues(0.dp)) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var slideDirection by remember { mutableIntStateOf(1) } // 1: 다음 달, -1: 이전 달

    val taskDates = remember { setOf(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5)) }

    Column(modifier = modifier.padding(innerPadding)) {
        // Month header
        CalendarHeader(currentMonth) { newMonth ->
            slideDirection = if (newMonth.monthValue > currentMonth.monthValue) 1 else -1
            currentMonth = newMonth
        }

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        // Weekday headers
        WeekSection()

        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        DaysGrid(currentMonth, selectedDate, taskDates, slideDirection) { newDate ->
            selectedDate = newDate
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected date display
        Text(text = "Selected Date: ${selectedDate}", fontSize = 18.sp)
    }
}

@Composable
private fun CalendarHeader(currentMonth: YearMonth, onMonthChange: (YearMonth, ) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DaysGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    taskDates: Set<LocalDate>,
    slideDirection: Int,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday

    val previousMonth = currentMonth.minusMonths(1)
    val nextMonth = currentMonth.plusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    val completedTaskCount = 3 // 완료된 태스크 개수
    val maxTaskCount = 10 // 최대 태스크 개수
    val lineWidth = getLineWidth(completedTaskCount, maxTaskCount) // 4dp에서 20dp 사이에서 완료율에 따라 길이 조절
    // 색상 계산 함수
    val indicatorColor = getIndicatorColor(lineWidth)
    AnimatedContent(
        targetState = currentMonth,
        transitionSpec = {
            if (slideDirection > 0) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }

        }
    ) { targetMonth ->
        Column {
            var dayCounter = 1
            for (week in 0 until 5) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 7) {
                        val day: Int?
                        val date: LocalDate

                        when {
                            week == 0 && i < firstDayOfMonth -> { // 이전 달 날짜
                                day = daysInPreviousMonth - (firstDayOfMonth - i - 1)
                                date = previousMonth.atDay(day)
                            }
                            dayCounter <= daysInMonth -> { // 이번 달 날짜
                                day = dayCounter++
                                date = currentMonth.atDay(day)
                            }
                            else -> { // 다음 달 날짜
                                day = (dayCounter - daysInMonth)
                                date = nextMonth.atDay(day)
                                dayCounter++
                            }
                        }


                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(enabled = day != null) {
                                    onDateSelected(
                                        date
                                    )
                                }
                                .background(
                                    color = when {
                                        date == selectedDate && date.month == currentMonth.month -> getBackgroundColorForDay(
                                            day,
                                            selectedDate,
                                            currentMonth.month
                                        )
                                        date.month == currentMonth.month -> Color.White
                                        else -> Color.LightGray // 이전/다음 달 날짜 색상
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString() ?: "",
                                    color = if (date.month == currentMonth.month) getTextColorForDay(day, currentMonth) else Color.Gray,
                                    fontSize = 16.sp
                                )
                                // Task indicator line
                                Spacer(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .width(
                                            if (hasTaskForDay(
                                                    day,
                                                    currentMonth,
                                                    taskDates
                                                )
                                            ) lineWidth else 0.dp
                                        )
                                        .background(
                                            if (hasTaskForDay(
                                                    day,
                                                    currentMonth,
                                                    taskDates
                                                )
                                            ) indicatorColor else Color.Transparent
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



fun getBackgroundColorForDay(day: Int?, selectedDate: LocalDate, currentMonth: Month): Color {
    val isDaySelected = day != null && selectedDate.dayOfMonth == day
    val isCurrentMonth = selectedDate.month == currentMonth

    return if (isDaySelected && isCurrentMonth) {
        Color.LightGray.copy(alpha = 0.3f)
    } else {
        Color.Transparent
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

@Preview(showBackground = true)
@Composable
fun CustomCalendarPreview() {
    Demo_calendarTheme {
        CustomCalendar(innerPadding = PaddingValues(0.dp))
    }
}