package io.github.binishmanandhar23.verticalcalendarlibrary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.binishmanandhar23.verticalcalendarlibrary.enum.WeekDayEnum
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import io.github.binishmanandhar23.verticalcalendarlibrary.viewmodel.CalendarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VerticalCalendarLibrary {
    var cellSize = 30.dp
    var startingMonthFromCurrentMonth = 60
    var weekDayEnd: WeekDayEnum = WeekDayEnum.SUNDAY
    lateinit var days: List<String>

    @Composable
    fun initialize(
        cellSize: Dp,
        startingMonthFromCurrentMonth: Int = 60,
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
        textStyleForWeekDays: TextStyle = TextStyle(),
        textStyleForHeading: TextStyle = TextStyle(),
        textStyleForBody: TextStyle = TextStyle(),
    ) {
        this.cellSize = cellSize
        this.startingMonthFromCurrentMonth = startingMonthFromCurrentMonth
        this.weekDayEnd = weekDayEnd
        this.days = listOfDays
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val calendarViewModel = CalendarViewModel(CalendarPagingRepo(startingMonthFromCurrentMonth))
        Column {
            TopHeader(listState = listState, coroutineScope = coroutineScope, textStyleForWeekDays)
            Body(listState = listState, calendarViewModel, textStyleForHeading, textStyleForBody)
        }
    }

    @Composable
    fun TopHeader(listState: LazyListState, coroutineScope: CoroutineScope, textStyleForWeekDays: TextStyle) {
        Column {
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .clickable {
                    coroutineScope.launch {
                        listState.animateScrollToItem(startingMonthFromCurrentMonth)
                    }
                }) {
                days.forEach {
                    Text(
                        it,
                        style = textStyleForWeekDays.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.size(cellSize)
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


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Body(listState: LazyListState, calendarViewModel: CalendarViewModel, textStyleForHeading: TextStyle, textStyleForBody: TextStyle) {
        val lazyCalendarData = calendarViewModel.getCalendarData().collectAsLazyPagingItems()
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(items = lazyCalendarData, itemContent = { _, value ->
                if (value != null) {
                    Text(
                        "${
                            value.month.getDisplayName(
                                org.threeten.bp.format.TextStyle.FULL,
                                Locale.getDefault()
                            )
                        } ${value.year}",
                        modifier = Modifier.padding(20.dp),
                        style = textStyleForHeading.copy(fontWeight = FontWeight.Bold)
                    )
                    PopulateCalendar(value = value, textStyleForBody)
                }
            })
        }
    }

    @Composable
    private fun PopulateCalendar(value: LocalDate, textStyleForBody: TextStyle) {
        val dates = ArrayList<LocalDate>()
        for (i in 1..value.lengthOfMonth()) {
            dates.add(value.withDayOfMonth(i))
        }
        val collectionOfHashMaps = ArrayList<HashMap<WeekDayEnum, Int>>()
        var hashMap = HashMap<WeekDayEnum, Int>()
        dates.forEachIndexed { i, date ->
            populateHashMap(hashMap, date) { weekDayHashMap ->
                if (date.dayOfWeek.value == weekDayEnd.index || i == dates.size - 1)
                    collectionOfHashMaps.add(weekDayHashMap).also { hashMap = HashMap() }
            }
        }
        collectionOfHashMaps.forEach { weekDayHashMap ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeekDayEnum.values().forEach {
                    if (weekDayHashMap[it] == null)
                        Spacer(modifier = Modifier.size(cellSize))
                    else
                        Text(
                            "${weekDayHashMap[it]}",
                            style = textStyleForBody.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            ), modifier = Modifier.size(cellSize)
                        )
                }

            }
        }
    }

    private inline fun populateHashMap(
        weekDayHashmap: HashMap<WeekDayEnum, Int>,
        localDate: LocalDate,
        returningHashMap: (weekDayHashMap: HashMap<WeekDayEnum, Int>) -> Unit
    ) {
        when (localDate.dayOfWeek.value) {
            WeekDayEnum.MONDAY.index -> weekDayHashmap[WeekDayEnum.MONDAY] = localDate.dayOfMonth
            WeekDayEnum.TUESDAY.index -> weekDayHashmap[WeekDayEnum.TUESDAY] = localDate.dayOfMonth
            WeekDayEnum.WEDNESDAY.index -> weekDayHashmap[WeekDayEnum.WEDNESDAY] =
                localDate.dayOfMonth
            WeekDayEnum.THURSDAY.index -> weekDayHashmap[WeekDayEnum.THURSDAY] =
                localDate.dayOfMonth
            WeekDayEnum.FRIDAY.index -> weekDayHashmap[WeekDayEnum.FRIDAY] = localDate.dayOfMonth
            WeekDayEnum.SATURDAY.index -> weekDayHashmap[WeekDayEnum.SATURDAY] =
                localDate.dayOfMonth
            WeekDayEnum.SUNDAY.index -> weekDayHashmap[WeekDayEnum.SUNDAY] = localDate.dayOfMonth
        }
        returningHashMap(weekDayHashmap)
    }
}