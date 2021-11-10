package io.github.binishmanandhar23.verticalcalendarlibrary

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.binishmanandhar23.verticalcalendarlibrary.enum.CalendarType
import io.github.binishmanandhar23.verticalcalendarlibrary.enum.WeekDayEnum
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarVisualModifications
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepoV2
import io.github.binishmanandhar23.verticalcalendarlibrary.util.ComposePagerSnapHelper
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils
import io.github.binishmanandhar23.verticalcalendarlibrary.viewmodel.CalendarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VerticalCalendarLibrary {
    var widthSize = 320.dp
    var miniCalendarListState: LazyListState? = null

    var scrollToIndexForExpandedCalendar = 60
    var scrollToIndexForMiniCalendar = 240

    var cellSize = 30.dp
    var startingMonthFromCurrentMonth = 60
    var weekDayEnd: WeekDayEnum = WeekDayEnum.SUNDAY
    lateinit var days: List<String>
    private val calendarType = MutableLiveData(CalendarType.MINI)

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun initialize(
        cellSize: Dp,
        listState: LazyListState,
        coroutineScope: CoroutineScope,
        mutableSelectedDate: MutableLiveData<LocalDate>,
        calendarDates: Collection<CalendarDay>?,
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
        widthSize: Dp,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: CalendarType,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        this.cellSize = cellSize
        this.startingMonthFromCurrentMonth = startingMonthFromCurrentMonth
        this.weekDayEnd = weekDayEnd
        this.days = listOfDays
        this.widthSize = widthSize
        val calendarPagingRepo = CalendarPagingRepo(startingMonthFromCurrentMonth)
        val selectedDateInState = mutableSelectedDate.observeAsState()
        val calendarTypeState = this.calendarType.observeAsState(calendarType)
        val calendarViewModel = CalendarViewModel(
            calendarPagingRepo = calendarPagingRepo,
            calendarPagingRepoV2 = CalendarPagingRepoV2()
        )

        Column {
            TopHeader(listState = listState, calendarVisualModifications, calendarTypeState.value)
            AnimatedContent(targetState = calendarTypeState.value, transitionSpec = {
                fadeIn(animationSpec = tween(300, 150)) with
                        fadeOut(animationSpec = tween(300)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState == CalendarType.FULL) {
                                keyframes {
                                    // Expand horizontally first.
                                    IntSize(targetSize.width, initialSize.height) at 250
                                    durationMillis = 300
                                }
                            } else {
                                keyframes {
                                    // Shrink vertically first.
                                    IntSize(initialSize.width, targetSize.height) at 250
                                    durationMillis = 300
                                }
                            }
                        }
            }) { value ->
                if (CalendarType.FULL == value) {
                    scrollToIndexForExpandedCalendar = VerticalCalendarUtils.getMonthIndex(selectedDateInState.value!!)
                    BodyV2(
                        listState = listState,
                        calendarDates,
                        selectedDateInState,
                        coroutineScope,
                        calendarViewModel,
                        calendarVisualModifications,
                        calendarTypeState,
                        onClick = {
                            mutableSelectedDate.value = it
                            onClick.invoke(it)
                        }
                    )
                }else {
                    scrollToIndexForMiniCalendar = VerticalCalendarUtils.getWeekIndex(selectedDateInState.value!!)
                    MiniCalendarBody(
                        calendarDates = calendarDates,
                        calendarViewModel,
                        selectedDateInState,
                        coroutineScope,
                        calendarVisualModifications,
                        calendarTypeState,
                        onClick = {
                            mutableSelectedDate.value = it
                            onClick.invoke(it)
                        })
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun TopHeader(
        listState: LazyListState,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: CalendarType
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
            AnimatedVisibility(visible = calendarType == CalendarType.FULL) {
                Divider(
                    color = Color.Gray, thickness = 1.dp, modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .alpha(0.5f)
                )
            }

        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Body(
        listState: LazyListState,
        calendarDates: Collection<CalendarDay>?,
        calendarViewModel: CalendarViewModel,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: () -> Unit
    ) {
        var selectedDate: LocalDate by remember { mutableStateOf(LocalDate.now()) }
        val hapticFeedback = LocalHapticFeedback.current
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
                        style = calendarVisualModifications.textStyleForHeading,
                        textAlign = TextAlign.Center
                    )
                    PopulateCalendar(
                        value = value,
                        selectedDate,
                        hapticFeedback,
                        calendarDates,
                        calendarVisualModifications,
                        onClick = {
                            selectedDate = it
                            onClick.invoke()
                        }
                    )
                }
            })
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun BodyV2(
        listState: LazyListState,
        calendarDates: Collection<CalendarDay>?,
        selectedDate: State<LocalDate?>,
        coroutineScope: CoroutineScope,
        calendarViewModel: CalendarViewModel,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: State<CalendarType>,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        val calendarData = calendarViewModel.getCalendarDataV2()?.observeAsState()
        LaunchedEffect(key1 = calendarData, block = {
            listState.scrollToItem(scrollToIndexForExpandedCalendar)
        })
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(items = calendarData?.value ?: ArrayList(), itemContent = { _, value ->
                Text(
                    "${
                        value.month.getDisplayName(
                            org.threeten.bp.format.TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${value.year}",
                    modifier = Modifier.padding(20.dp),
                    style = calendarVisualModifications.textStyleForHeading,
                    textAlign = TextAlign.Center
                )
                PopulateCalendar(
                    value = value,
                    selectedDate.value!!,
                    hapticFeedback,
                    calendarDates,
                    calendarVisualModifications,
                    onClick = {
                        onClick.invoke(it)
                    }
                )
            })
        }

    }

    @Composable
    private fun PopulateCalendar(
        value: LocalDate,
        selectedDate: LocalDate,
        hapticFeedback: HapticFeedback,
        calendarDates: Collection<CalendarDay>?,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val dates = ArrayList<LocalDate>()
        for (i in 1..value.lengthOfMonth()) {
            dates.add(value.withDayOfMonth(i))
        }
        val collectionOfHashMaps = ArrayList<HashMap<WeekDayEnum, LocalDate>>()
        var hashMap = HashMap<WeekDayEnum, LocalDate>()
        dates.forEachIndexed { i, date ->
            populateHashMap(hashMap, date) { weekDayHashMap ->
                if (date.dayOfWeek.value == weekDayEnd.index || i == dates.size - 1)
                    collectionOfHashMaps.add(weekDayHashMap).also { hashMap = HashMap() }
            }
        }
        collectionOfHashMaps.forEach { weekDayHashMap ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeekDayEnum.values().forEach {
                    if (weekDayHashMap[it] == null)
                        Spacer(modifier = Modifier.size(cellSize))
                    else {
                        val today = weekDayHashMap[it] == LocalDate.now()
                        val modifier = Modifier
                            .background(
                                color = if (selectedDate == weekDayHashMap[it]) calendarVisualModifications.todayBackgroundColor else Color.Transparent,
                                CircleShape
                            )
                            .size(cellSize)
                            .wrapContentSize(Alignment.Center)
                            .clickable(MutableInteractionSource(), null) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onClick.invoke(weekDayHashMap[it]!!)
                            }
                        if (calendarDates == null)
                            Text(
                                "${weekDayHashMap[it]?.dayOfMonth}",
                                style = calendarVisualModifications.textStyleForBody.copy(
                                    color = if (selectedDate == weekDayHashMap[it]) Color.White else if (today) calendarVisualModifications.textStyleForToday.color else calendarVisualModifications.textStyleForBody.color
                                ),
                                modifier = modifier,
                                textAlign = TextAlign.Center,
                            )
                        else {
                            var isPopulated = false
                            calendarDates.forEach CalendarDates@{ calendarDate ->
                                if (calendarDate.date == weekDayHashMap[it]) {
                                    isPopulated = true
                                    return@CalendarDates
                                }
                            }
                            Text(
                                "${weekDayHashMap[it]?.dayOfMonth}",
                                style = when {
                                    selectedDate == weekDayHashMap[it] -> calendarVisualModifications.textStyleForBody.copy(
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
                }

            }
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

    @Composable
    fun MiniCalendarBody(
        calendarDates: Collection<CalendarDay>?,
        calendarViewModel: CalendarViewModel,
        selectedDate: State<LocalDate?>,
        coroutineScope: CoroutineScope,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: State<CalendarType>,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        var previousSelectedIndex = 240
        val hapticFeedback = LocalHapticFeedback.current
        val calendarData = calendarViewModel.getCalendarDataV2ForMini()?.observeAsState()

        LaunchedEffect(key1 = calendarData, block = {
            miniCalendarListState?.scrollToItem(scrollToIndexForMiniCalendar)
        })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ComposePagerSnapHelper(widthSize) { lazyListState ->
                this@VerticalCalendarLibrary.miniCalendarListState = lazyListState
                val visibleIndex = lazyListState.firstVisibleItemIndex
                if (visibleIndex != previousSelectedIndex && !lazyListState.isScrollInProgress) {
                    calendarData?.value?.let {
                        selectedDate.value?.dayOfWeek?.value?.let { value ->
                            val visibleMonday = it[visibleIndex].with(DayOfWeek.MONDAY)
                            val difference =
                                value - visibleMonday.dayOfWeek.value
                            val newSelectedDate = visibleMonday.plusDays((difference).toLong())
                            onClick.invoke(newSelectedDate)
                        }
                    }
                    previousSelectedIndex = lazyListState.firstVisibleItemIndex
                }
                LazyRow(
                    state = lazyListState, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
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
            .size(cellSize)
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
}