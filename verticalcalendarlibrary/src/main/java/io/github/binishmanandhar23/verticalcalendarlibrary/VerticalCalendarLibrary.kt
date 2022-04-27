package io.github.binishmanandhar23.verticalcalendarlibrary

import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.binishmanandhar23.verticalcalendarlibrary.enums.CalendarType
import io.github.binishmanandhar23.verticalcalendarlibrary.enums.WeekDayEnum
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarVisualModifications
import io.github.binishmanandhar23.verticalcalendarlibrary.model.PopulatingData
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepoV2
import io.github.binishmanandhar23.verticalcalendarlibrary.util.ComposePagerSnapHelper
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils.NUMBEROFMONTHS
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils.NUMBEROFWEEKS
import io.github.binishmanandhar23.verticalcalendarlibrary.viewmodel.CalendarViewModel
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import java.util.*

class VerticalCalendarLibrary {
    var widthSize = 320.dp
    var miniCalendarListState: LazyListState? = null

    var scrollToIndexForExpandedCalendar = NUMBEROFMONTHS
    var scrollToIndexForMiniCalendar = NUMBEROFWEEKS

    var fromGoToTodayButton = false

    var cellSize = 25.dp
    var startingMonthFromCurrentMonth = 60
    var weekDayEnd: WeekDayEnum = WeekDayEnum.SUNDAY
    lateinit var days: List<String>

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun initialize(
        cellSize: Dp = 25.dp,
        listState: LazyListState,
        mutableSelectedDate: MutableLiveData<LocalDate>,
        calendarDates: PopulatingData = PopulatingData(),
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
        widthSize: Dp = 320.dp,
        fullCalendarHeight: Dp,
        calendarVisualModifications: CalendarVisualModifications = CalendarVisualModifications(),
        calendarTypeState: CalendarType = CalendarType.FULL,
        onClickInitiatedFrom: MutableLiveData<CalendarType> = MutableLiveData(CalendarType.FULL),
        onClick: ((selectedDate: LocalDate) -> Unit)? = null
    ) {
        this.cellSize = cellSize
        this.startingMonthFromCurrentMonth = startingMonthFromCurrentMonth
        this.weekDayEnd = weekDayEnd
        this.days = listOfDays
        this.widthSize = widthSize
        val calendarPagingRepo = CalendarPagingRepo(startingMonthFromCurrentMonth)
        val selectedDateInState = mutableSelectedDate.observeAsState()

        val calendarViewModel = CalendarViewModel(
            calendarPagingRepo = calendarPagingRepo,
            calendarPagingRepoV2 = CalendarPagingRepoV2()
        )

        Column {
            TopHeader(listState = listState, calendarVisualModifications, calendarTypeState)
            AnimatedContent(targetState = calendarTypeState, transitionSpec = {
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
                    scrollToIndexForExpandedCalendar =
                        VerticalCalendarUtils.getMonthIndex(selectedDateInState.value!!)
                    BodyV2(
                        listState = listState,
                        calendarDates,
                        selectedDateInState,
                        calendarViewModel,
                        calendarVisualModifications,
                        calendarTypeState,
                        fullCalendarHeight = fullCalendarHeight,
                        onClick = {
                            onClickInitiatedFrom.value = CalendarType.FULL
                            mutableSelectedDate.value = it
                            onClick?.invoke(it)
                        }
                    )
                } else {
                    scrollToIndexForMiniCalendar =
                        VerticalCalendarUtils.getWeekIndex(selectedDateInState.value!!)
                    MiniCalendarBody(
                        calendarDates = calendarDates,
                        calendarViewModel,
                        selectedDateInState,
                        calendarVisualModifications,
                        calendarTypeState,
                        onClickInitiatedFrom,
                        onClick = {
                            onClickInitiatedFrom.value = CalendarType.MINI
                            mutableSelectedDate.value = it
                            onClick?.invoke(it)
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
        calendarType: CalendarType,
        addDivider: Boolean = false
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), verticalAlignment = Alignment.Bottom
            ) {
                days.forEach {
                    Text(
                        it.uppercase(),
                        style = calendarVisualModifications.textStyleForWeekDays,
                        modifier = Modifier.size(width = cellSize, height = cellSize - 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (addDivider)
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
        calendarDates: PopulatingData?,
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
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
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

    @OptIn(
        ExperimentalFoundationApi::class
    )
    @Composable
    fun BodyV2(
        listState: LazyListState,
        calendarDates: PopulatingData?,
        selectedDate: State<LocalDate?>,
        calendarViewModel: CalendarViewModel,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: CalendarType,
        fullCalendarHeight: Dp,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        val newListState = rememberLazyListState()
        val calendarData = calendarViewModel.getCalendarDataV2()?.observeAsState()
        LaunchedEffect(key1 = calendarData, block = {
            newListState.scrollToItem(if (scrollToIndexForExpandedCalendar < 0) 0 else scrollToIndexForExpandedCalendar)
        })
        val isInScroll by derivedStateOf { newListState.isScrollInProgress }
        LaunchedEffect(key1 = isInScroll) {
            snapshotFlow { newListState.firstVisibleItemScrollOffset }
                .distinctUntilChanged()
                .filter { it > 0 }
                .collect {
                    newListState.animateScrollToItem(if (it <= 450) newListState.firstVisibleItemIndex else newListState.firstVisibleItemIndex + 1)
                }

        }
        LazyColumn(
            state = newListState, modifier = Modifier
                .fillMaxWidth()
                .height(fullCalendarHeight),
            flingBehavior = StockFlingBehaviours.getAndroidNativeScroll()
        ) {
            itemsIndexed(items = calendarData?.value ?: ArrayList(), itemContent = { _, value ->
                Text(
                    "${
                        value.month.getDisplayName(
                            org.threeten.bp.format.TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${value.year}".uppercase(),
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 15.dp),
                    style = calendarVisualModifications.textStyleForHeading,
                    textAlign = TextAlign.Center
                )
                PopulateCalendar(
                    value = value,
                    selectedDate.value!!,
                    hapticFeedback,
                    calendarDates,
                    calendarVisualModifications,
                    onClick = onClick
                )
            })
        }

    }

    @Composable
    private fun PopulateCalendar(
        value: LocalDate,
        selectedDate: LocalDate,
        hapticFeedback: HapticFeedback,
        calendarDates: PopulatingData?,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit,
        numberOfRows: ((rows: Int) -> Unit)? = null
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
        numberOfRows?.invoke(collectionOfHashMaps.size)
        collectionOfHashMaps.forEach { weekDayHashMap ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
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
                        if (calendarDates == null)
                            Column(verticalArrangement = Arrangement.Center) {
                                Box {
                                    Text(
                                        "${weekDayHashMap[it]?.dayOfMonth}",
                                        style = calendarVisualModifications.textStyleForBody.copy(
                                            color = if (selectedDate == weekDayHashMap[it]) Color.White else if (today) calendarVisualModifications.textStyleForToday.color else calendarVisualModifications.textStyleForBody.color
                                        ),
                                        modifier = modifier.clickable(
                                            MutableInteractionSource(),
                                            null
                                        ) {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onClick.invoke(weekDayHashMap[it]!!)
                                        },
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        else
                            DateDesign(
                                modifier = modifier,
                                calendarDates = calendarDates,
                                date = weekDayHashMap[it],
                                selectedDate = selectedDate,
                                today = today,
                                calendarVisualModifications = calendarVisualModifications
                            ) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onClick.invoke(weekDayHashMap[it]!!)
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

    /*private fun MiniCalendarIndexWork(selectedDate: LocalDate?, calendarData: State<ArrayList<LocalDate>?>?,onClick: (selectedDate: LocalDate) -> Unit){
        val visibleIndex =
            if (selectedDate == LocalDate.now() && fromGoToTodayButton) VerticalCalendarUtils.getWeekIndex(
                LocalDate.now()
            ) else VerticalCalendarUtils.getWeekIndex(selectedDate = selectedDate!!)
        fromGoToTodayButton = false
        if (visibleIndex != scrollToIndexForMiniCalendar && miniCalendarListState?.isScrollInProgress == false) {
            calendarData?.value?.let {
                selectedDate?.dayOfWeek?.value?.let { value ->
                    val visibleMonday = it[visibleIndex].with(DayOfWeek.MONDAY)
                    val difference =
                        value - visibleMonday.dayOfWeek.value
                    val newSelectedDate = visibleMonday.plusDays((difference).toLong())
                    onClick.invoke(newSelectedDate) // Commented to mitigate multiple date selection issue when selecting dates from Full calendar
                    Log.i(
                        "InteractionCheck",
                        "OldSelectedDate: $selectedDate  NewSelectedDate: ${newSelectedDate.dayOfMonth}"
                    )
                }
            }
        }
    }*/

    @Composable
    fun MiniCalendarBody(
        calendarDates: PopulatingData?,
        calendarViewModel: CalendarViewModel,
        selectedDate: State<LocalDate?>,
        calendarVisualModifications: CalendarVisualModifications,
        calendarType: CalendarType,
        onClickInitiatedFrom: MutableLiveData<CalendarType>,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        val calendarData = calendarViewModel.getCalendarDataV2ForMini()?.observeAsState()
        val onClickInitiatedFromState = onClickInitiatedFrom.observeAsState()

        LaunchedEffect(key2 = calendarData, key1 = selectedDate.value, block = {
            miniCalendarListState?.scrollToItem(if (scrollToIndexForMiniCalendar < 0) 0 else scrollToIndexForMiniCalendar)
        })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ComposePagerSnapHelper(widthSize) { lazyListState ->
                this@VerticalCalendarLibrary.miniCalendarListState = lazyListState
                val visibleIndex =
                    if (selectedDate.value == LocalDate.now() && fromGoToTodayButton) VerticalCalendarUtils.getWeekIndex(
                        LocalDate.now()
                    ) else lazyListState.firstVisibleItemIndex
                fromGoToTodayButton = false
                if (visibleIndex != scrollToIndexForMiniCalendar && !lazyListState.isScrollInProgress) {
                    calendarData?.value?.let {
                        selectedDate.value?.dayOfWeek?.value?.let { value ->
                            val visibleMonday = it[visibleIndex].with(DayOfWeek.MONDAY)
                            val difference =
                                value - visibleMonday.dayOfWeek.value
                            val newSelectedDate = visibleMonday.plusDays((difference).toLong())
                            //-----------------------------------------------//
                            if (onClickInitiatedFromState.value == CalendarType.MINI)
                                onClick.invoke(newSelectedDate)
                            onClickInitiatedFrom.value = CalendarType.MINI
                            // Important for separating clicks from Full Calendar or Mini Calendar specially in Medication screen
                        }
                    }
                }
                LazyRow(
                    state = lazyListState, modifier = Modifier
                        .fillMaxWidth(),
                    flingBehavior = StockFlingBehaviours.getAndroidNativeScroll()
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
        calendarDates: PopulatingData?,
        selectedDate: LocalDate,
        hapticFeedback: HapticFeedback,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: (selectedDate: LocalDate) -> Unit
    ) {
        val today = date == LocalDate.now()
        val modifier = Modifier
            .padding(bottom = 5.dp)
            .background(
                color = if (selectedDate == date) calendarVisualModifications.todayBackgroundColor else Color.Transparent,
                CircleShape
            )
            .size(cellSize)
            .wrapContentSize(
                Alignment.Center
            )
        if (calendarDates == null)
            Text(
                "${date.dayOfMonth}",
                style = calendarVisualModifications.textStyleForBody.copy(
                    color = if (selectedDate == date) Color.White else if (today) calendarVisualModifications.textStyleForToday.color else calendarVisualModifications.textStyleForBody.color
                ),
                modifier = modifier.clickable(MutableInteractionSource(), null) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick.invoke(date)
                },
                textAlign = TextAlign.Center,
            )
        else
            DateDesign(
                modifier = modifier,
                calendarDates = calendarDates,
                date = date,
                selectedDate = selectedDate,
                today = today,
                calendarVisualModifications = calendarVisualModifications
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick.invoke(date)
            }
    }

    @Composable
    private fun DateDesign(
        modifier: Modifier,
        calendarDates: PopulatingData,
        date: LocalDate?,
        selectedDate: LocalDate,
        today: Boolean,
        calendarVisualModifications: CalendarVisualModifications,
        onClick: () -> Unit
    ) {
        val isPopulated = VerticalCalendarUtils.isPopulated(
            calendarDates.collectionOfDates,
            date
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .clickable(MutableInteractionSource(), null) {
                        onClick.invoke()
                    }) {
                Text(
                    "${date?.dayOfMonth}",
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
                if (calendarVisualModifications.highlightImageId != null && calendarDates.highlightedDates?.contains(
                        CalendarDay.from(date)!!
                    ) == true
                )
                    Image(
                        painterResource(id = calendarVisualModifications.highlightImageId!!),
                        contentDescription = "Highlighted",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .offset(x = (-5).dp, y = 5.dp)
                    )
            }
            IndicatorDesign(
                date,
                isPopulated,
                calendarDates
            )
        }
    }

    @Composable
    private fun IndicatorDesign(
        currentDate: LocalDate?,
        isPopulated: Boolean,
        calendarDates: PopulatingData
    ) {
        if (isPopulated)
            Row {
                var indicators = 0
                calendarDates.collectionOfDates?.forEach {
                    it.populatedDate.forEach { calendarDay ->
                        if (calendarDay.date == currentDate) {
                            if (indicators > 0)
                                Spacer(modifier = Modifier.size(2.dp))
                            Box(modifier = it.customModifier).also {
                                indicators++
                            }
                        }
                    }
                }
            }
        else
            Spacer(modifier = Modifier.size(5.dp))
    }
}