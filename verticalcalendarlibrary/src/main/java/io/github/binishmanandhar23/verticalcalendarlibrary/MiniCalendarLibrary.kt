package io.github.binishmanandhar23.verticalcalendarlibrary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.github.binishmanandhar23.verticalcalendarlibrary.enum.WeekDayEnum
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarVisualModifications
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepoV2
import io.github.binishmanandhar23.verticalcalendarlibrary.util.ComposePagerSnapHelper
import io.github.binishmanandhar23.verticalcalendarlibrary.viewmodel.CalendarViewModel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MiniCalendarLibrary {
    var widthSize = 320.dp
    var cellSize = 30.dp
    var startingMonthFromCurrentMonth = 60
    var listState: LazyListState? = null
    var weekDayEnd: WeekDayEnum = WeekDayEnum.SUNDAY
    lateinit var days: List<String>

    @Composable
    fun initialize(
        widthSize: Dp,
        calendarDates: Collection<CalendarDay>?,
        startingMonthFromCurrentMonth: Int = 60,
        mutableSelectedDate: MutableLiveData<LocalDate>,
        listOfDays: List<String> = listOf(
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"
        ),
        weekDayEnd: WeekDayEnum = WeekDayEnum.SUNDAY,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        this.widthSize = widthSize
        this.days = listOfDays

        val calendarPagingRepo = CalendarPagingRepo(startingMonthFromCurrentMonth)
        val calendarViewModel = CalendarViewModel(
            calendarPagingRepo = calendarPagingRepo,
            calendarPagingRepoV2 = CalendarPagingRepoV2()
        )

        Column {
            TopHeader(calendarVisualModifications)
            Body(
                calendarDates = calendarDates,
                calendarViewModel,
                mutableSelectedDate,
                calendarVisualModifications,
                onClick = {
                    mutableSelectedDate.value = it
                    onClick.invoke(it)
                })
        }
    }

    @Composable
    fun TopHeader(
        calendarVisualModifications: CalendarVisualModifications
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                days.forEach {
                    Text(
                        it,
                        style = calendarVisualModifications.textStyleForWeekDays,
                        modifier = Modifier.size(cellSize),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Divider(
                color = Color.Gray, thickness = 1.dp, modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .alpha(0.5f)
            )
        }
    }

    @Composable
    fun Body(
        calendarDates: Collection<CalendarDay>?,
        calendarViewModel: CalendarViewModel,
        mutableSelectedDate: MutableLiveData<LocalDate>,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        var previousSelectedIndex = 240
        val selectedDate = mutableSelectedDate.observeAsState()
        val hapticFeedback = LocalHapticFeedback.current
        val calendarData = calendarViewModel.getCalendarDataV2ForMini()?.observeAsState()

        LaunchedEffect(key1 = calendarData, block = {
            listState?.scrollToItem(240)
        })
        ComposePagerSnapHelper(widthSize) { lazyListState ->
            listState = lazyListState
            val visibleIndex = listState?.firstVisibleItemIndex
            if (visibleIndex != null && visibleIndex != previousSelectedIndex && listState?.isScrollInProgress == false) {
                calendarData?.value?.let {
                    selectedDate.value?.dayOfWeek?.value?.let { value ->
                        val difference = value - it[visibleIndex].with(DayOfWeek.MONDAY).dayOfWeek.value
                        val newSelectedDate = it[visibleIndex].plusDays((difference - 1).toLong())
                        onClick.invoke(newSelectedDate)
                    }
                }
                previousSelectedIndex = listState?.firstVisibleItemIndex!!
            }
            LazyRow(
                state = lazyListState, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                items(items = calendarData?.value ?: ArrayList()) { item ->
                    item.with(DayOfWeek.MONDAY)
                    Row(
                        modifier = Modifier.width(widthSize),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PopulateDates(
                            date = item.with(DayOfWeek.MONDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.TUESDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.WEDNESDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.THURSDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.FRIDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.SATURDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                        PopulateDates(
                            date = item.with(DayOfWeek.SUNDAY),
                            calendarDates = calendarDates,
                            selectedDate = selectedDate.value!!,
                            hapticFeedback = hapticFeedback,
                            calendarVisualModifications = calendarVisualModifications,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PopulateDates(
        date: LocalDate,
        calendarDates: Collection<CalendarDay>?,
        selectedDate: LocalDate,
        hapticFeedback: HapticFeedback,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val today = date == LocalDate.now()
        val modifier = Modifier
            .background(
                color = if (selectedDate == date) calendarVisualModifications.todayBackgroundColor else Color.Transparent,
                CircleShape
            )
            .size(cellSize + 10.dp)
            .wrapContentSize(
                Alignment.Center
            )
            .clickable(MutableInteractionSource(), null) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick.invoke(date)
            }
        if (calendarDates == null)
            Text(
                "${date.dayOfMonth}",
                style = calendarVisualModifications.textStyleForBody.copy(
                    color = if (selectedDate == date) Color.White else if (today) calendarVisualModifications.textStyleForToday.color else calendarVisualModifications.textStyleForBody.color
                ),
                modifier = modifier,
                textAlign = TextAlign.Center,
            )
        else {
            var isPopulated = false
            calendarDates.forEach CalendarDates@{ calendarDate ->
                if (calendarDate.date == date) {
                    isPopulated = true
                    return@CalendarDates
                }
            }
            Text(
                "${date.dayOfMonth}",
                style = when {
                    selectedDate == date -> calendarVisualModifications.textStyleForBody.copy(
                        color = Color.White
                    )
                    today -> calendarVisualModifications.textStyleForToday
                    isPopulated -> calendarVisualModifications.textStyleForSelectedDays
                    else -> calendarVisualModifications.textStyleForBody
                },
                modifier = modifier,
                textAlign = TextAlign.Center
            )
        }
    }

    private inline fun populateHashMap(
        weekDayHashmap: HashMap<WeekDayEnum, LocalDate>,
        localDate: LocalDate,
        returningHashMap: (weekDayHashMap: HashMap<WeekDayEnum, LocalDate>) -> Unit
    ) {
        when (localDate.dayOfWeek.value) {
            WeekDayEnum.MONDAY.index -> weekDayHashmap[WeekDayEnum.MONDAY] = localDate
            WeekDayEnum.TUESDAY.index -> weekDayHashmap[WeekDayEnum.TUESDAY] = localDate
            WeekDayEnum.WEDNESDAY.index -> weekDayHashmap[WeekDayEnum.WEDNESDAY] =
                localDate
            WeekDayEnum.THURSDAY.index -> weekDayHashmap[WeekDayEnum.THURSDAY] =
                localDate
            WeekDayEnum.FRIDAY.index -> weekDayHashmap[WeekDayEnum.FRIDAY] = localDate
            WeekDayEnum.SATURDAY.index -> weekDayHashmap[WeekDayEnum.SATURDAY] =
                localDate
            WeekDayEnum.SUNDAY.index -> weekDayHashmap[WeekDayEnum.SUNDAY] = localDate
        }
        returningHashMap(weekDayHashmap)
    }
}